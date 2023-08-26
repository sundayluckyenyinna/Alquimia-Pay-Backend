package com.gms.alquimiapay.test;

import com.gms.alquimiapay.webhook.dto.circle.CircleWebhookTransferDTO;
import com.google.gson.Gson;
import kong.unirest.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;

public class Test {
    public static void main(String[] args) {
        String json = "{\n" +
                "\"Type\" : \"Notification\",\n" +
                "    \"MessageId\" : \"a3ea1aed-68e5-5041-87f1-47fda6087fb3\",\n" +
                "\"TopicArn\" : \"arn:aws:sns:us-east-1:908968368384:sandbox_platform-notifications-topic\",\n" +
                "   \"Message\" : \"{\\\"clientId\\\":\\\"f4d7575c-a3b9-4301-995e-2335519c7652\\\",\\\"notificationType\\\":\\\"transfers\\\",\\\"version\\\":1,\\\"customAttributes\\\":{\\\"clientId\\\":\\\"f4d7575c-a3b9-4301-995e-2335519c7652\\\"},\\\"transfer\\\":{\\\"id\\\":\\\"d2dd8755-8310-4543-8c9a-1b497ab61a61\\\",\\\"source\\\":{\\\"type\\\":\\\"wallet\\\",\\\"id\\\":\\\"1016339088\\\"},\\\"destination\\\":{\\\"type\\\":\\\"blockchain\\\",\\\"address\\\":\\\"0x730097604fb847b5760d9ac3b314a5ca8badf6c0\\\",\\\"chain\\\":\\\"ETH\\\"},\\\"amount\\\":{\\\"amount\\\":\\\"3.00\\\",\\\"currency\\\":\\\"USD\\\"},\\\"status\\\":\\\"pending\\\",\\\"createDate\\\":\\\"2023-06-24T10:59:56.965Z\\\"}}\",\n" +
                "\"Timestamp\" : \"2023-06-24T10:59:57.143Z\",\n" +
                "\"SignatureVersion\" : \"1\",\n" +
                "\"Signature\" : \"e32DZo0vgdrjEhoRKBBfmo5Al9LzMKlTbepriJcQceD95gUztkY1all3HOKhzmh8/M1+QnQ+imsVxLxc3MlgsX7TU7mIczgG7CPuIH3/oItSHlUiFeC3W045EP4JrsXabRADX0SVSelWDVG7sAxIybCsQOBYaMXaVDg3XEA9xGrnAx2y3f97phqM1bVki6zGEGmirWdROrYhfWmx13Ekf1gEB49IiAw6pDEYjGqjqaueFeuqXegOI1et0BZFVv/zaeG/1w0G6EvG7bBkaMd9zwfYIMtI/su97N4vWVt3SdkzrZ+LcbhMi1ruT/YWXIuDvmyWH8HoiaqaIAYZajonDQ==\",\n" +
                "\"SigningCertURL\" : \"https://sns.us-east-1.amazonaws.com/SimpleNotificationService-01d088a6f77103d0fe307c0069e40ed6.pem\",\n" +
                "\"UnsubscribeURL\" : \"https://sns.us-east-1.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:us-east-1:908968368384:sandbox_platform-notifications-topic:762fe0b0-8b66-4b5b-91fb-cdbe1357e075\",\n" +
                "\"MessageAttributes\" : {\n" +
                "\"clientId\" : {\"Type\":\"String\",\"Value\":\"f4d7575c-a3b9-4301-995e-2335519c7652\"}\n" +
                "}\n" +
                "}";
        JSONObject object = new JSONObject(json);
        String messageJson = object.getString("Message");
        JSONObject messageObject = new JSONObject(messageJson);
        JSONObject object1 = messageObject.getJSONObject("transfer");
        CircleWebhookTransferDTO dto = new Gson().fromJson(object1.toString(), CircleWebhookTransferDTO.class);
        System.out.println(dto.getAmount().getAmount());
    }

    public static String generateAccountNumber(){
        String nowString = String.valueOf(System.currentTimeMillis());
        String[] array = nowString.split("");
        Collections.reverse(Arrays.asList(array));
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 10; i++){
            builder.append(String.valueOf(array[i]));
        }
        return builder.toString();
    }
}
