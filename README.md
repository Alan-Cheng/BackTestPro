# 回測平台 BackTest PRO

### 功能

* 關鍵字搜尋股票代號、設定投資參數
* 選定投資條件，計算定期定額累積報酬率

* 累積報酬率以動態折線圖顯示：
    1. 由 Java 撰寫的後端搜集證交所股價資訊
    2. 以 RESTful API 提供前端所需資訊
* 會員功能：
    1. 以 Spring Security 做權限管理，不同權限可瀏覽不同頁面
    2. 以 JWT(JSON Web Tokens)管理使用者權限

## DEMO
>
>
>
>### 1. 投資功能
>
> * 設定投資參數：
>
>![BTP投資設定頁](https://github.com/Alan-Cheng/BackTestPro/blob/main/demo/investment.png?raw=true "投資設定頁面")
>
> * 選定並送出投資參數後，可看到定期定額累積報酬率與指數報酬率對比：
>
>![BTP報酬率頁面](https://github.com/Alan-Cheng/BackTestPro/blob/main/demo/chart.png?raw=true "報酬率頁面")
>
>
>### 2. 會員功能
>
> * 帳號、密碼與權限，儲存於DB中用於驗證：
>
>![BTP登入頁](https://github.com/Alan-Cheng/BackTestPro/blob/main/demo/login.png?raw=true "登入頁面")
>
> * 根據JWT取得帳戶權限展示：
>
>![BTP權限](https://github.com/Alan-Cheng/BackTestPro/blob/main/demo/role_detail.png?raw=true "權限展示")
>
> * 若無權限則無法取得、瀏覽特定頁面資料：
>
>![BTP未授權頁面](https://github.com/Alan-Cheng/BackTestPro/blob/main/demo/role.png?raw=true "未授權頁面")
>
>




## 技術工具



>| 功能  | 技術 |
>| ------------- |:-------------:|
>| 後端MVC       | Spring Boot      |
>| 驗證與權限       | Spring Security, JWT      |
>| 資料庫       | MySQL      |
>| API 風格       | RESTful      |
>| 前端       | Angular      |



---

