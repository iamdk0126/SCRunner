Lightweight Katalon Executor (輕量化 Katalon 執行器)
📖 專案簡介 (Project Overview)
本專案旨在開發一套 「輕量級、無介面依賴」 的 Katalon Studio 專案執行引擎。

原本的 Katalon Studio 基於 Eclipse 框架，啟動緩慢且極度消耗系統資源（RAM 常駐 1GB~4GB）。本執行器的核心目標是：完全不更動現有 Katalon 專案檔案，直接讀取並解析現有的測試資產，提供極速的啟動時間（< 5秒）、超低的資源佔用，並完美整合進 CI/CD 流水線。

主要應用場景：

針對現有的 OPENMAM2 (或其他 Katalon 專案) 進行極速自動化測試。

透過 CLI 模式整合至 Jenkins / GitHub Actions 進行背景執行。

支援多執行緒（Multi-threading）平行跑測試，大幅縮短整體測試時間。

✨ 核心特色 (Core Features)
🟢 100% 檔案相容：直接讀取 Katalon 的 .prj, .tc, .ts, .rs, .glbl 檔案，無需轉檔。

🟢 強大解析引擎：自動將 Katalon 獨有的 BASIC (屬性組合) 定位器與 XPATH 完美轉換為 Selenium WebDriver 格式。

🟢 動態編譯支援：支援 Katalon Custom Keywords 腳本的動態加載與編譯。

🟢 平行測試調度：支援 Test Suite Collection，可自訂執行緒數 (Max Instances) 平行處理測試。

🟢 內建 Headless：預設支援 Chrome Headless 模式，背景執行不搶奪游標。

🟢 專業報告產出：測試完畢自動收集截圖與 Log，產出視覺化的 PDF 測試報告。

🛠️ 技術架構 (Tech Stack)
開發工具：IntelliJ IDEA Community (開源免費版)

核心語言：Java / Kotlin

建置系統：Gradle

腳本引擎：Apache Groovy (groovy-all 4.x)

自動化驅動：Selenium WebDriver (4.x)

XML 解析器：Dom4j + Jaxen (處理 Object Repository)

📅 開發時程與進度 (Development Roadmap)
本專案分為四個主要開發階段。目前進度處於 Phase 1 (已完成)，即將進入 Phase 2。

📍 階段一：專案解析與基礎建設 (Foundation) - [✅ 100% 完成]
目標：讓程式具備讀懂 Katalon 專案資料夾結構與底層設定的能力。

[x] 1.1 專案目錄掃描器：遍歷指定專案目錄，將測試資產載入內存。

[x] 1.2 物件庫 (.rs) 解析器：深度解析 XML，並將 Katalon BASIC / XPATH 轉為 Selenium By 物件。(排除過重的影像定位器)

[x] 1.3 全域變數解析器：利用 Groovy 引擎讀取 Profiles/default.glbl，自動轉型並快取變數 (String, Integer 等)。

📍 階段二：執行引擎與動態橋接 (Execution Engine) - [🚀 下一步驟]
目標：讓單一個 Katalon 測試腳本 (.groovy) 能在不報錯的情況下順利跑完。

[ ] 2.1 動態載入自定義關鍵字 (Custom Keywords)：掃描並使用 GroovyClassLoader 動態編譯 Keywords/ 底下的腳本。

[ ] 2.2 WebUI 模擬橋接層 (Bridge)：實作極簡版的 WebUI 類別，封裝 Selenium 動作 (click, setText, openBrowser)。

[ ] 2.3 GroovyShell 整合測試：將全域變數與 WebUI 注入腳本引擎，執行單一 Login 腳本。

📍 階段三：測試調度與平行處理 (Orchestration & Parallelism)
目標：支援 Test Suite Collection，並能安全地「同時跑多路」測試。

[ ] 3.1 測試套件解析：解析 .ts 與 .tsc，處理測試執行順序與資料綁定 (Data-Driven)。

[ ] 3.2 執行緒池 (Thread Pool) 實作：建立可設定路數的排程器。

[ ] 3.3 資源隔離機制：實作 ThreadLocal<WebDriver> 確保並行測試的瀏覽器實例互不干擾。

📍 階段四：使用者介面、報告與 CLI (UI, Reporting & CLI)
目標：打包成企業級的自動化測試工具。

[ ] 4.1 CLI 雙啟動模式：實作指令參數 (如 -noGui, -projectPath, -parallel=4) 供 CI/CD 呼叫。

[ ] 4.2 自動化 PDF 報告：攔截測試錯誤與截圖，利用 OpenPDF 生成測試結果圖表與報告。

[ ] 4.3 輕量化桌面 UI：使用 Compose for Desktop 開發圖形化操作介面 (即時 Log 顯示、專案樹狀圖)。

快速啟動 (Quick Start)
Quick 1. 環境需求
   安裝 JDK 17

安裝 IntelliJ IDEA (Community 版即可)

Quick 2. 專案建置
   下載本專案源碼並使用 IntelliJ IDEA 開啟。

點擊 build.gradle 旁的重新整理按鈕 (Load Gradle Changes) 下載相依套件。

開啟 Main.java，將 projectRoot 變數修改為你的 Katalon 專案路徑 (例如：OPENMAM2 的絕對路徑)。

執行 Main.java，觀察 Console 輸出，確認物件庫與變數是否成功載入。