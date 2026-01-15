# ![YooKassa](https://yookassa.ru/favicon.ico) Payment Service

## Architecture

🌐 Client (YooKassa)
       │
       │  POST /webhook
       │ 
       ▼
   ┌─────────────────────────┐
   │        Nginx Proxy      │
   │─────────────────────────│
   │ 🔑 proxy_set_header    │
   │   X-Real-IP $remote_addr│
   └─────────────────────────┘
       │
       │  Forwarded POST /webhook
       │  X-Real-IP: $remote_addr
       ▼
┌──────────────────────────────┐
│      Spring Boot App         │
│                              |
│ ✅ Reads X-Real-IP           |
│ ✅ Validates IP (YooKassa)   │
│ ✅ Processes webhook         │
└──────────────────────────────┘



## Nginx Proxy Configuration

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
