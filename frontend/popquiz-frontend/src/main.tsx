import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router";
import "@ant-design/v5-patch-for-react-19";
import zhCN from "antd/locale/zh_CN";
import { ConfigProvider } from "antd";
import { MainRouter } from "./routes.tsx";
import "./main.css";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <MainRouter />
      </BrowserRouter>
    </ConfigProvider>
  </StrictMode>,
);
