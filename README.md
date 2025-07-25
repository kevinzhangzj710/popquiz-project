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

需要保证 **ffmpeg** 可执行文件在你的PATH中，或者在该工作目录下。
对于Windows，只需要一个下载一个预先构建的 **ffmpeg.exe** 到工作目录下即可。
[推荐下载网址](https://www.gyan.dev/ffmpeg/builds/)

在该工作目录下执行以下命令，这里假设解压出来的 jar 文件名和配置文件名，可以根据实际情况调整。

```sh
java -jar popquiz-full-app.jar --spring.config.location="application.properties"
```

可以访问 `localhost:8080` 或者 `127.0.0.1:8080` 来访问服务，只要能访问到主机 8080 端口的设备，都可以访问到本服务。

## application.properties配置内容

**基础配置**

spring.application.name=popquiz-backend

spring.datasource.url=jdbc:hsqldb:mydatabase;hsqldb.lock_file=false

spring.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver

spring.servlet.multipart.max-file-size=100MB

spring.servlet.multipart.max-request-size=100MB

springdoc.api-docs.path=/api-doc


**#aliyun ISI key**

请联系我们获取阿里云密钥


**#.txt files of ppt and pdf storage path**

file.storage.location=runtime/pdfFile


**#.txt files of voice storage path**

file.upload-dir=${user.dir}/runtime/uploadsVoice



## 目前实现的功能

- 登录注册
- 组织者新建课程，听众加入课程，听众退出课程
- 组织者新建演讲
- 演讲者在演讲中手动创建题目
- 听众作答题目，看到正确结果
- 用户在演讲页，题目页提交评论
- 上传文档
- AI生成题目，根据文档和演讲语音
- 演讲语音实时转文字
- 教师的统计数据
- 学生的统计数据
