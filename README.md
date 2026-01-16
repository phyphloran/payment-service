# ![YooKassa](https://static.tildacdn.com/tild3965-3039-4464-b037-633164346132/image.png) 

<h1>
  <img
    src="https://yookassa.ru/favicon.ico"
    width="24"
    style="vertical-align: middle;"
  />
  Payment Service
</h1>

## Architecture

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
      <img src="https://github.com/phyphloran/payment-service/blob/main/architecture.png" alt="Architecture" width="500"/>
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
The backend accepts webhooks only from YooKassa, since all traffic goes through Nginx.
Important: direct access to the backend from outside must be blocked to prevent bypassing the IP check.

---

## 🔄 CI/CD Pipeline with GitHub Actions
The project includes an automated CI/CD pipeline that tests, builds, and deploys the service using GitHub Actions and Docker.

### Pipeline Stages:
1. Tests Stage
Runs automated tests to ensure code quality and correctness.

2. Build Stage
Builds a Docker image of the service only if all tests pass successfully.

3. Deploy Stage
Deploys the built Docker image to the target environment only after a successful build.

### ❗ Pipeline Behavior
The build and deploy stages are executed only if the tests stage completes successfully. Any test failure stops the pipeline.

[![workflow](https://github.com/phyphloran/payment-service/actions/workflows/workflow.yml/badge.svg)](https://github.com/phyphloran/payment-service/actions/workflows/workflow.yml)
