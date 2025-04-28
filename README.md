# 記帳社群 - 後端

## 專案介紹
這是一個結合個人記帳與社群分享的記帳平台，使用者可以記錄日常收支，公開消費紀錄，追蹤他人帳戶，並透過優惠提醒，幫助自己取得更好的消費性價比。

## 技術架構
- Java 17
- Spring Boot 3
- MySQL
- JWT 驗證
- Redis

## 功能地圖 (Functional Map)
![functional-map](https://github.com/user-attachments/assets/38006be5-f78a-476d-9387-19138fdedb57)


### 使用者基本流程
![image](https://github.com/user-attachments/assets/48843bca-460d-41eb-9698-2400c547ef42)


## 資料表設計 (ER Diagram)
![er-diagram](https://github.com/user-attachments/assets/892cd904-41e8-4f70-b25e-7ed66c4c917c)



## API設計
- POST /user/login (登入)
- POST /user/register (註冊)
- GET /user/verify (驗證信箱)
- POST /user/resendMail (重新發送驗證信)
- GET /member (取得使用者資訊)
- PUT /member (修改使用者資訊)
- PATCH /member/password (修改使用者密碼)
- GET /account (取得使用者所有帳戶列表)
- POST /account (新增帳戶)
- GET /account/{id} (取得特定帳戶詳細資料)
- PUT /account/{id} (更新特定帳戶詳細資料)
- DELETE /account/{id} (刪除特定帳戶)
- GET /account/{accountId}/records (取得特定帳戶交易資料)
- POST /account/{accountId}/records (新增特定帳戶交易資料)
- DELETE /account/{accountId}/records/{recordId} (刪除特定帳戶交易資料)
- GET /category (取得收入/支出所有類別)


## 環境設定
- `mvn clean install`
- 啟動 MySQL
- `java -jar target/demo.jar`
