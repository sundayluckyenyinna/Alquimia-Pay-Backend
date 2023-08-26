package com.gms.alquimiapay.scheduler.reporting.job;

import com.gms.alquimiapay.constants.CronExpression;
import com.gms.alquimiapay.constants.ModelStatus;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.account.model.AccountDeposit;
import com.gms.alquimiapay.modules.account.model.VirtualAccountCache;
import com.gms.alquimiapay.modules.account.repository.IAccountDepositRepository;
import com.gms.alquimiapay.modules.account.repository.IAccountRepository;
import com.gms.alquimiapay.modules.report.util.SimpleExcelFactory;
import com.gms.alquimiapay.modules.transaction.model.TransactionEntry;
import com.gms.alquimiapay.modules.transaction.repository.ITransactionEntryRepository;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.modules.user.repository.IUserRepository;
import com.gms.alquimiapay.modules.wallet.model.GmsWalletCache;
import com.gms.alquimiapay.modules.wallet.repository.IGmsWalletCacheRepository;
import com.gms.alquimiapay.scheduler.reporting.dto.GmsCurrentMonthSignup;
import com.gms.alquimiapay.util.AttachmentData;
import com.gms.alquimiapay.util.BigDecimalUtil;
import com.gms.alquimiapay.util.EmailMessenger;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SimpleReportScheduler {

    @Value("${gms.admin.email}")
    private String adminRecipient;

    private final IUserRepository userRepository;
    private final IGmsWalletCacheRepository walletCacheRepository;
    private final IAccountRepository accountRepository;
    private final ITransactionEntryRepository transactionEntryRepository;
    private final IAccountDepositRepository accountDepositRepository;
    private final EmailMessenger emailMessenger;

    private static final Gson JSON = new Gson();
    private static final String WORK_BOOK_NAME_SUFFIX = "GMS-MINI-REPORT";

    @Scheduled(cron = CronExpression.EVERY_1ST_DAY_OF_MONTH_AT_MIDNIGHT)
    public void produceCurrentMonthReports(){

        // Get to the start of the last month at Mid-night (12:00am)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonthAtMidnight = now.minusMonths(1);

        List<GmsCurrentMonthSignup> users = getAllCurrentSignupFromDate(lastMonthAtMidnight);
        List<TransactionEntry> entries = getAllTransactionEntryFromDate(lastMonthAtMidnight);
        List<AccountDeposit> accountDeposits = getAllAccountDepositFromDate(lastMonthAtMidnight);

        // Create the Excel workbook and corresponding sheets
        Map<String, List<? extends Object>> map = new HashMap<>();
        if(!users.isEmpty())
            map.put("Signup Users", users);
        if(!entries.isEmpty())
            map.put("Transaction Entries", entries);
        if(!accountDeposits.isEmpty())
            map.put("Account Deposits", accountDeposits);

        String workbookName = String.valueOf(lastMonthAtMidnight.getYear())
                .concat(StringValues.HYPHEN).concat(lastMonthAtMidnight.getMonth().name())
                .concat(StringValues.HYPHEN).concat(WORK_BOOK_NAME_SUFFIX);

        if(!map.isEmpty()) {
            String workbookPath = SimpleExcelFactory.getInstance().buildExcelDocumentFromSheetListReturnAbsPath(workbookName, map);

            // Build the mail data
            long completedTransfers = getCountOfStatusFromEntries(entries, ModelStatus.COMPLETE.name());
            long pendingTransfers = getCountOfStatusFromEntries(entries, ModelStatus.PENDING.name());
            long failedTransfers = getCountOfStatusFromEntries(entries, ModelStatus.FAILED.name());

            Map<String, String> data = new HashMap<>();
            data.put("user", String.valueOf(users.size()));
            data.put("entries", String.valueOf(entries.size()));
            data.put("completedTransfer", String.valueOf(completedTransfers));
            data.put("pendingTransfers", String.valueOf(pendingTransfers));
            data.put("failedTransfers", String.valueOf(failedTransfers));
            data.put("completedPercent", getFormattedPercentageOfStatus(entries.size(), completedTransfers));
            data.put("pendingPercent", getFormattedPercentageOfStatus(entries.size(), pendingTransfers));
            data.put("failedPercent", getFormattedPercentageOfStatus(entries.size(), failedTransfers));
            data.put("deposit", String.valueOf(accountDeposits.size()));

            // Send Excel as attachment to business mail.
            AttachmentData attachmentData = new AttachmentData();
            attachmentData.setAbsolutePath(workbookPath);
            attachmentData.setName(workbookName.concat(".xlsx"));
            attachmentData.setDescription("GMS Admin Monthly Report");

            List<AttachmentData> attachmentDataList = Collections.singletonList(attachmentData);
            emailMessenger.sendMailWithDataAndAttachment(adminRecipient, "admin-report", "GMS Monthly Report", data, attachmentDataList);
        }
        else{
            emailMessenger.sendMessage(adminRecipient, "admin-report-empty", "GMS Monthly report");
        }
    }


    private List<GmsCurrentMonthSignup> getAllCurrentSignupFromDate(LocalDateTime dateTime){
        List<GmsUser> lastMonthSignups = userRepository.findAllUsersFromDateTime(dateTime);
        AtomicInteger count = new AtomicInteger(0);
        return lastMonthSignups.stream()
                .map(user -> {
                    GmsCurrentMonthSignup currentMonthSignup = JSON.fromJson(JSON.toJson(user), GmsCurrentMonthSignup.class);
                    currentMonthSignup.setSn(String.valueOf(count.incrementAndGet()));

                    GmsWalletCache walletCache = walletCacheRepository.findByOwnerEmail(user.getEmailAddress());
                    VirtualAccountCache accountCache = accountRepository.findByInternalCustomerEmail(user.getEmailAddress());
                    if (walletCache != null)
                        currentMonthSignup.setWalletId(walletCache.getWalletId());
                    if (accountCache != null)
                        currentMonthSignup.setWireVirtualAccountNumber(accountCache.getAccountNumber());

                    return currentMonthSignup;
                })
                .collect(Collectors.toList());
    }

    private List<TransactionEntry> getAllTransactionEntryFromDate(LocalDateTime dateTime){
        return transactionEntryRepository.findAllTransactionEntryFromDate(dateTime);
    }

    private List<AccountDeposit> getAllAccountDepositFromDate(LocalDateTime dateTime){
        return accountDepositRepository.findAllAccountDepositFromDate(dateTime);
    }

    private long getCountOfStatusFromEntries(List<TransactionEntry> entries, String status){
        return entries.stream().filter(e -> e.getExternalStatus().equalsIgnoreCase(status)).count();
    }

    private String getFormattedPercentageOfStatus(long total, long count){
        BigDecimal totalDec = BigDecimalUtil.from(String.valueOf(total));
        BigDecimal countDec = BigDecimalUtil.from(String.valueOf(count));
        BigDecimal percent = countDec.multiply(new BigDecimal(100)).divide(totalDec, 2, RoundingMode.HALF_UP);
        return percent.toString().concat(StringValues.PERCENT_SIGN);
    }

    private String formatWithoutDec(long value){
        return NumberFormat.getInstance().format(value);
    }
}
