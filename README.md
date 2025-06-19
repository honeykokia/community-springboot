# 記帳社群 - 後端 (Community Accounting - Backend)

## 📌 專案介紹
這是一個結合個人記帳與社群分享的記帳平台，使用者可以記錄日常收支，公開消費紀錄，追蹤他人帳戶，並透過優惠提醒，幫助自己取得更好的消費性價比。

## 🔗 線上 Demo
- [Demo 網址（前端入口）](https://weirong.site/projectA/)
> ＊首次開啟時，如遇載入稍慢為 GCP主機喚醒過程，請稍等數秒。

## 🛠 技術架構
- Java 17
- Spring Boot 3
- MySQL
- JWT 驗證
- Redis (驗證信使用)

## 🗺 功能地圖 (Functional Map)
> 系統主要功能模組。
![functional-map](https://github.com/user-attachments/assets/01d57bb0-1f42-44a5-a3df-9e420df45297)



## 🚶 使用者基本流程 (User Flow)
> 使用者從登入到操作各功能的基本路徑。
![image](https://github.com/user-attachments/assets/48843bca-460d-41eb-9698-2400c547ef42)


## 🛢 資料表設計 (ER Diagram)
> 後端資料表之間的關聯設計。
![er-diagram](https://github.com/user-attachments/assets/892cd904-41e8-4f70-b25e-7ed66c4c917c)

## ☁️ 部署架構 (Deployment Setup)

- 本專案後端部署於 GCP VM（Google Cloud Platform）。
- 使用 Nginx 進行反向代理設定：
  - 前端靜態資源 `/projectA` 指向 Vue3 build 目錄
  - API 請求 `/api/projectA/` 轉發至 Spring Boot 應用
- 資料庫使用 MySQL，架設於同一台 GCP VM。
- Redis 亦部署於同台主機，用於管理驗證信與登入狀態。

> 目前系統已可正常於雲端環境運作，支援 HTTPS 通訊。

## 📡 API設計
### 使用者系統 (User System)
- POST /user/login (登入)
- POST /user/register (註冊)
- GET /user/verify (驗證信箱)
- POST /user/resendMail (重新發送驗證信)
- GET /member (取得使用者資訊)
- PUT /member (修改使用者資訊)
- PATCH /member/password (修改使用者密碼)
- POST /forgetPassword/request (忘記密碼請求驗證碼)
- POST /forgetPassword/verify (驗證碼驗證)
- PUT /forgetPassword/reset (使用者修改密碼)

### 帳戶系統 (Account System)
- GET /account (取得使用者所有帳戶列表)
- POST /account (新增帳戶)
- GET /account/{id} (取得特定帳戶詳細資料)
- PUT /account/{id} (更新特定帳戶詳細資料)
- DELETE /account/{id} (刪除特定帳戶)
- GET /account/{accountId}/records (取得特定帳戶交易資料)
- POST /account/{accountId}/records (新增特定帳戶交易資料)
- DELETE /account/{accountId}/records/{recordId} (刪除特定帳戶交易資料)

### 類別系統 (Category System)
- GET /category (取得收入/支出所有類別)


## ⚙️ 環境設定
- `mvn clean install`
- 啟動 MySQL
- `java -jar target/demo.jar`

