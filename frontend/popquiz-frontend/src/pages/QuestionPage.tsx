import { HomePageLayout } from "./Dashboard.tsx";
import { useParams } from "react-router";
import { ErrorBack } from "../ui/utils.tsx";
import { QuestionShowcase, type question_data } from "../ui/Questions.tsx";
import { useEffect, useState } from "react";
import { Button, message, Radio, Typography } from "antd";
import { $fetch } from "../api/api-utils.ts";
import dayjs from "dayjs";
import { useAtomValue } from "jotai";
import { user_id_atom } from "../states/user.ts";

export function QuestionPage() {
  const { question_id } = useParams();
  const q_id = parseInt(question_id ? question_id : "-1");
  return (
    <HomePageLayout
      children={
        question_id ? <QuestionShowcase question_id={q_id} /> : <ErrorBack />
      }
    />
  );
}

export function TeacherQuestionPage() {
  const { question_id } = useParams();
  const q_id = parseInt(question_id ? question_id : "-1");
  const user_id = useAtomValue(user_id_atom);
  const [question, setQuestion] = useState<question_data>();
  const [messageApi, contextHolder] = message.useMessage();

  async function fetchQuestion() {
    const question_id = q_id;
    const { data, error } = await $fetch.GET("/api/getQuestionById", {
      params: {
        query: {
          question_id,
        },
      },
    });
    if (error) {
      messageApi.error("获取题目出错");
    } else {
      setQuestion(data);
    }
  }

  useEffect(() => {
    fetchQuestion();
  }, [question_id, user_id]);

  return (
    <HomePageLayout
      children={
        !question_id ? (
          <ErrorBack />
        ) : (
          <>
            {/*lack of API for teacher statics*/}
            {contextHolder}
            {!question && <ErrorBack />}
            {question && (
              <>
                <Typography.Title level={2}>小测验：</Typography.Title>
                <Typography.Title level={4}>
                  题目：{question.question}
                </Typography.Title>
                <div>
                  开始作答时间：
                  {dayjs(question.start_time).format("YYYY-MM-DD HH:mm:ss")}
                </div>
                <div>
                  截止作答时间：
                  {dayjs(question.end_time).format("YYYY-MM-DD HH:mm:ss")}
                </div>
                <Typography.Title level={4}>选项：</Typography.Title>
                <Radio.Group
                  value={""}
                  style={{ display: "flex", flexDirection: "column", gap: "8" }}
                  options={question.selection.split("\n", 4).map((answer) => ({
                    value: answer[0],
                    label: answer,
                  }))}
                />
              </>
            )}
          </>
        )
      }
    />
  );
}

export function StudentQuestionPage() {
  const { question_id } = useParams();
  const q_id = parseInt(question_id ? question_id : "-1");
  const user_id = useAtomValue(user_id_atom);
  const [question, setQuestion] = useState<question_data>();
  const [submitted, setSubmitted] = useState<boolean>(false);
  const [selected, setSelected] = useState<string>("");
  const [messageApi, contextHolder] = message.useMessage();

  async function fetchQuestion() {
    const question_id = q_id;
    const { data, error } = await $fetch.GET("/api/getQuestionById", {
      params: {
        query: {
          question_id,
        },
      },
    });
    if (error) {
      messageApi.error("获取题目出错");
      return;
    } else {
      setQuestion(data);
    }
    fetchSubmittion();
  }

  async function fetchSubmittion() {
    const question_id = q_id;
    if (!question) return;
    const { data, error } = await $fetch.GET("/api/getSubmitId", {
      params: {
        query: {
          quiz_id: question_id,
          user_id: user_id,
        },
      },
    });
    if (error || data === -1) {
      messageApi.error("获取作答失败");
      // logic for time out
      if (dayjs(question.end_time).isBefore(dayjs())) {
        setSubmitted(true);
      }
      return;
    }
    const submit_id = data;
    const res2 = await $fetch.GET("/api/getSubmitById", {
      params: {
        query: {
          submit_id,
        },
      },
    });
    if (res2.error || res2.data === undefined) {
      messageApi.error("获取作答失败！");
      return;
    }
    if (res2.data.answer.length > 0) {
      setSelected(res2.data.answer);
      setSubmitted(true);
    }
  }

  async function submitQuestion() {
    const question_id = q_id;
    const { data, error } = await $fetch.POST("/api/createSubmit", {
      body: {
        question_id,
        user_id,
        answer: selected,
      },
    });
    if (error || data === -1) {
      messageApi.error("提交作答失败");
      return;
    }
    messageApi.success("提交成功");
    setSubmitted(true);
  }

  useEffect(() => {
    fetchQuestion();
  }, [question_id, user_id]);

  return (
    <HomePageLayout
      children={
        !question_id ? (
          <ErrorBack />
        ) : (
          <>
            {contextHolder}
            {question && (
              <>
                <Typography.Title level={2}>小测验：</Typography.Title>
                <Typography.Title level={4}>
                  题目：{question.question}
                </Typography.Title>
                <div>
                  开始作答时间：
                  {dayjs(question.start_time).format("YYYY-MM-DD HH:mm:ss")}
                </div>
                <div>
                  截止作答时间：
                  {dayjs(question.end_time).format("YYYY-MM-DD HH:mm:ss")}
                </div>
                <Typography.Title level={4}>选项：</Typography.Title>
                <Radio.Group
                  style={{ display: "flex", flexDirection: "column", gap: "8" }}
                  onChange={(e) => {
                    if (!submitted) {
                      setSelected(e.target.value);
                    }
                  }}
                  value={selected}
                  options={question.selection.split("\n", 4).map((answer) => ({
                    value: answer[0],
                    label: answer,
                  }))}
                />
                {submitted && (
                  <Typography.Title level={5}>
                    正确答案:{question.answer}
                    <br />
                    你的答案:{selected}
                  </Typography.Title>
                )}
                <Button
                  type={"primary"}
                  onClick={submitQuestion}
                  disabled={submitted}
                >
                  提交
                </Button>
              </>
            )}
          </>
        )
      }
    />
  );
}
