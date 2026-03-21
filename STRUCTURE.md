universe/

├── README.md

├── docker-compose.yml

├── docker-compose.dev.yml

├── .env.example

│

├── mobile/                          # React Native App

│   ├── src/

│   │   ├── screens/

│   │   │   ├── auth/

│   │   │   ├── student/

│   │   │   └── lecturer/

│   │   ├── components/

│   │   ├── navigation/

│   │   ├── services/

│   │   ├── store/

│   │   └── utils/

│   ├── Dockerfile

│   ├── .dockerignore

│   ├── package.json

│   └── app.json

│

├── web-admin/                       # Next.js Admin Portal

│   ├── src/

│   │   ├── app/

│   │   ├── components/

│   │   ├── lib/

│   │   └── services/

│   ├── Dockerfile

│   ├── Dockerfile.dev

│   ├── .dockerignore

│   ├── next.config.js

│   └── package.json

│

├── backend/                         # NestJS API

│   ├── src/

│   │   ├── modules/

│   │   │   ├── auth/

│   │   │   ├── users/

│   │   │   ├── attendance/

│   │   │   ├── courses/

│   │   │   ├── grades/

│   │   │   ├── notifications/

│   │   │   └── chat/

│   │   ├── common/

│   │   │   ├── guards/

│   │   │   ├── decorators/

│   │   │   └── filters/

│   │   └── main.ts

│   ├── test/

│   ├── Dockerfile

│   ├── Dockerfile.dev

│   ├── .dockerignore

│   └── package.json

│

├── ai-service/                      # Python Chatbot

│   ├── app/

│   │   ├── routers/

│   │   └── services/

│   ├── Dockerfile

│   ├── Dockerfile.dev

│   ├── .dockerignore

│   └── requirements.txt

│

├── docs/

│   ├── uml/

│   ├── api/

│   └── database/

│

└── scripts/

&#x20;   ├── seed.sql

&#x20;   └── init-mongo.js

