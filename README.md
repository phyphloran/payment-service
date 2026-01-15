# ![YooKassa](https://static.tildacdn.com/tild3965-3039-4464-b037-633164346132/image.png) 

---

<h1>
  <svg
    width="32"
    height="32"
    viewBox="0 0 100 100"
    xmlns="http://www.w3.org/2000/svg"
    style="vertical-align: middle; margin-right: 8px;"
  >
    <circle cx="50" cy="50" r="45" fill="#7A3EFF"/>
    <text x="50" y="58" text-anchor="middle" font-size="40" fill="white">Y</text>
  </svg>
  Payment Service
</h1>

---

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

[![workflow](https://github.com/phyphloran/payment-service/actions/workflows/workflow.yml/badge.svg)](https://github.com/phyphloran/payment-service/actions/workflows/workflow.yml)
