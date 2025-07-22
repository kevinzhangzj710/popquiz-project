# popquiz-project

PopQuiz - using AI to give smart pop quiz about any content, a quick and easy way to check the audience "have you paid attention".

## 项目成员

张骏 kevinzhangzj
王庆宇 3241672910
朱志恒 2601880763

## 运行说明

访问 **Github Actions** 栏目中的 **Full App Build** 任务，下载最近的成功构建成品(**Artifacts**)，名称为 **popquiz-full-app**

下载后解压出其中内容，目前只包含一个 jar，请将 jar 文件移动到一个 **空白工作目录** 中。

请准备好一个 **application.properties** 同样放在该工作目录中。

> **💡 Tip**:  
> 如果你是评测项目的教师，请联系团队，团队会提供一份用于演示的配置文件，包含所需的云服务相关 API KEY。

在该工作目录下执行以下命令，这里假设解压出来的 jar 文件名和配置文件名，可以根据实际情况调整。

```sh
java -jar popquiz-full-app.jar --spring.config.location="application.properties"
```

可以访问 `localhost:8080` 或者 `127.0.0.1:8080` 来访问服务，只要能访问到主机 8080 端口的设备，都可以访问到本服务。
