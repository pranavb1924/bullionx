# BullionX — Stock Trading Simulator

A microservices‑based **trade simulation platform** built with **Spring Boot** and **Angular**. BullionX helps new retail investors learn the stock market without the noise and complexity of professional tools. It focuses on a clear UI, sensible defaults, and a small set of core actions so beginners can practice with confidence.

---

## Goals

* **De‑cluttered experience** for first‑time traders
* **Hands‑on learning** with realistic flows (place orders, track P/L)
* **Safe sandbox** (no real money) with virtual cash

---

## Architecture

**Microservices** with a thin **API Gateway** in front. Services expose REST endpoints; the Angular client only talks to the gateway. There are **two services**: **Auth** and **Portfolio**.

```
┌──────────────┐      ┌──────────────────┐
│   Angular    │◄────►│    API Gateway   │────┬───────────────┐
│    (Web)     │      │  (Spring Cloud)  │    │               │
└─────▲────────┘      └───────▲──────────┘    │               │
      │                       │               │               │
      │  HTTPS                │ Routes        │               │
      │                       │ /auth,        │               │
      │                       │ /portfolio    │               │
      │                       │               │               │
      │                ┌──────┴───────┐  ┌────┴────────┐
      │                │  Auth Svc    │  │ Portfolio   │
      │                │  (Spring)    │  │  Svc        │
      │                └──────▲───────┘  └────▲────────┘
      │                       │  JWT          │ Trades, P/L
      │                       │               │ Holdings
      │                ┌──────┴───────┐  ┌────┴────────┐
      │                │ PostgreSQL   │  │ PostgreSQL  │
      │                └──────────────┘  └─────────────┘
```
