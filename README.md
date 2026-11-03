# Финансовая аналитика (Finance Analytics)

[![SonarCloud Analysis](https://github.com/Rimadorio/Finance_analytics/actions/workflows/sonar.yml/badge.svg)](https://github.com/Rimadorio/Finance_analytics/actions/workflows/sonar.yml)
<!-- Сюда можно добавить бейдж Quality Gate из SonarCloud. 
     Зайдите в проект на SonarCloud -> Project Information -> Badges -> выберите Quality Gate -> Markdown -->
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Rimadorio_finance-analytics&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Rimadorio_finance-analytics)

Веб-приложение для финансового анализа с бэкендом на Java (Spring Boot). 
Позволяет рассчитывать доходность вкладов и симулировать досрочное погашение ипотеки.

## 🚀 Функционал

### 1. Расчет доходности вклада
*   Ввод суммы, срока и процентной ставки.
*   Поддержка вкладов **с капитализацией** (сложный процент) и **без** (простой процент).
*   Расчет эффективной годовой ставки.
*   Форматирование ввода денежных сумм (разделители тысяч).

### 2. Досрочное погашение ипотеки
*   Расчет экономии при досрочном погашении.
*   Настройка суммы и периодичности досрочных платежей (раз в N месяцев).
*   Настройка периода досрочного погашения.
*   Две стратегии: **сокращение срока** или **сокращение платежа**.
*   Генерация подробного графика платежей (с досрочками и без).
*   Отображение экономии на процентах.

## 🛠 Стек технологий

*   **Backend:** Java 17, Spring Boot 3, Spring Web, Spring Validation
*   **Frontend:** HTML5, CSS3, Vanilla JavaScript (Fetch API)
*   **Сборка:** Gradle (Kotlin DSL)
*   **Тестирование:** JUnit 5
*   **CI/CD & Анализ кода:** GitHub Actions, SonarCloud

## 🏃‍♂️ Как запустить локально

1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/Rimadorio/Finance_analytics.git
