# ![YooKassa](https://static.tildacdn.com/tild3965-3039-4464-b037-633164346132/image.png) 

<h1>
  <img
    src="https://yookassa.ru/favicon.ico"
    width="24"
    style="vertical-align: middle;"
  />
  Payment Service
</h1>

A Spring Boot service for integration with the **YooKassa** payment system, designed for secure creation and processing of online payments.

The service implements the full payment lifecycle and provides the following features:

## ✨ Features

- **Idempotent payment creation**  
  Ensures that repeated requests with the same idempotence key do not result in duplicate payments.

- **Payment status persistence and management**  
  Payments are stored in the database and transition through the following states:  
  `PENDING`, `SUCCEEDED`, `CANCELLED`.

- **YooKassa webhook processing**  
  Supports `payment.succeeded` and `payment.canceled` events with validation of event data and payment consistency.

- **YooKassa IP address validation**  
  Webhook notifications are accepted only from trusted YooKassa IP addresses, protecting the service from request spoofing.

- **Protection against duplicate and invalid notifications**  
  The service validates event type, payment status, and amount to prevent repeated processing and inconsistent state updates.

## ⚙️ Technologies

- `Java 21`
- `Spring Boot 3+`
- `Spring-boot-starter-validation`
- `Spring Data JPA`
- `PostgreSQL`
- `Docker`
- `JUnit 5`
- `Mockito`
- `GitHub Actions (CI/CD for testing, build and deploy)`
- `Nginx (reverse proxy, SSL termination)`

## 🏗️ Architecture

<table>
  <tr>
    <td valign="top">
      <strong>Webhook Flow</strong>
      <ol>
        <li>Client (YooKassa) sends POST /webhook</li>
        <li>Nginx forwards request with real IP</li>
        <li>Spring Boot validates IP and processes webhook</li>
      </ol>
    </td>
    <td>
      <img 
        src="https://github.com/phyphloran/payment-service/blob/main/architecture_pics/webhook_event.JPEG" 
        alt="Webhook Event Flow" 
        width="500"
      />
    </td>
  </tr>

  <tr>
    <td valign="top">
      <strong>Payment Creation Flow</strong>
      <ol>
        <li>Client sends POST /payments</li>
        <li>Spring Boot creates payment request</li>
        <li>YooKassa returns payment confirmation</li>
      </ol>
    </td>
    <td>
      <img 
        src="https://github.com/phyphloran/payment-service/blob/main/architecture_pics/create_payment.JPEG" 
        alt="Create Payment Flow" 
        width="500"
      />
    </td>
  </tr>
</table>


---

## Nginx Proxy Configuration

---

```
location / {
    proxy_pass http://localhost:8080;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header Host $host;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header X-Forwarded-Port $server_port;
    proxy_connect_timeout 60s;
    proxy_send_timeout 60s;
    proxy_read_timeout 60s;
    proxy_buffering off;
}
```

#### The key directive:

```
proxy_set_header X-Real-IP $remote_addr;
```

#### What does it give?

Thanks to this directive, Nginx forwards the real client IP to the backend.
The backend accepts webhooks only from YooKassa, since all traffic goes through Nginx.<br>
Important: direct access to the backend from outside must be blocked to prevent bypassing the IP check.

---

## 🔄 CI/CD Pipeline with GitHub Actions
The project includes an automated CI/CD pipeline that tests, builds, and deploys the service using GitHub Actions and Docker.

### Pipeline Stages:
1. Tests Stage<br>
Runs automated tests to ensure code quality and correctness.

2. Build Stage<br>
Builds a Docker image of the service only if all tests pass successfully.

3. Deploy Stage<br>
Deploys the built Docker image to the target environment only after a successful build.

### ❗ Pipeline Behavior
The build and deploy stages are executed only if the tests stage completes successfully. Any test failure stops the pipeline.

[![workflow](https://github.com/phyphloran/payment-service/actions/workflows/workflow.yml/badge.svg)](https://github.com/phyphloran/payment-service/actions/workflows/workflow.yml)
