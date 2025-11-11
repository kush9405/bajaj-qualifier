package com.example.bajaj_qualifier;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class AppRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Application started, beginning the process...");

        RestTemplate restTemplate = new RestTemplate();
        String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Kushagra Agarwal");
        requestBody.put("regNo", "PES2UG22CS275");
        requestBody.put("email", "pes2ug22cs275@pesu.pes.edu");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        System.out.println("Sending initial request to generate webhook...");
        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(generateWebhookUrl, requestEntity, Map.class);

        Map<String, String> responseBody = responseEntity.getBody();
        String accessToken = responseBody.get("accessToken");
        String webhookUrl = responseBody.get("webhook");

        System.out.println("Received accessToken and webhook URL successfully.");
        System.out.println("Webhook URL: " + webhookUrl);


        String finalQuery = "SELECT p.amount AS SALARY, e.first_name || ' ' || e.last_name AS NAME, strftime('%Y', 'now') - strftime('%Y', e.dob) - (strftime('%m-%d', 'now') < strftime('%m-%d', e.dob)) AS AGE, d.department_name AS DEPARTMENT_NAME FROM payments p JOIN employee e ON p.emp_id = e.emp_id JOIN department d ON e.department = d.department_id WHERE CAST(strftime('%d', p.payment_time) AS INTEGER) != 1 ORDER BY p.amount DESC LIMIT 1;";

        HttpHeaders submissionHeaders = new HttpHeaders();
        submissionHeaders.setContentType(MediaType.APPLICATION_JSON);
        submissionHeaders.set("Authorization", accessToken); // Use the token from the first response

        Map<String, String> submissionBody = new HashMap<>();
        submissionBody.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> submissionEntity = new HttpEntity<>(submissionBody, submissionHeaders);

        System.out.println("Submitting the final SQL query...");

        ResponseEntity<String> submissionResponse = restTemplate.postForEntity(webhookUrl, submissionEntity, String.class);

        System.out.println("Submission response status: " + submissionResponse.getStatusCode());
        System.out.println("Submission response body: " + submissionResponse.getBody());
        System.out.println("Task completed successfully!");
    }
}