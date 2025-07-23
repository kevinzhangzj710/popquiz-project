import { useParams } from "react-router";
import { HomePageLayout } from "./Dashboard.tsx";
import { ErrorBack } from "../ui/utils.tsx";
import { type speech_data } from "../ui/Speeches.tsx";
import { useAtomValue } from "jotai/index";
import { user_id_atom } from "../states/user.ts";
import { useEffect, useState } from "react";
import {
  Button,
  Divider,
  List,
  message,
  Space,
  Typography,
  Upload,
} from "antd";
import { type comment_data, CommentList } from "../ui/Comments.tsx";
import { $fetch } from "../api/api-utils.ts";
import { Recorder } from "../ui/Recorder.tsx";
import { type question_data, QuestionListItem } from "../ui/Questions.tsx";
import {
  ModalForm,
  ProFormDigit,
  ProFormText,
  ProFormTextArea,
  ProFormTimePicker,
} from "@ant-design/pro-components";
import dayjs from "dayjs";
import { UploadOutlined } from "@ant-design/icons";

export function SpeechPage() {
  const { speech_id } = useParams();
  const s_id = parseInt(speech_id ? speech_id : "-1");
  const user_id = useAtomValue(user_id_atom);
  const [userRole, setUserRole] = useState(-1);
  const [messageApi, contextHolder] = message.useMessage();
  const isStudent = userRole === 1;
  const isOrganizerOrSpeaker = userRole === 0;
  const isUnknown = userRole === -1;

  async function fetchRole() {
    const { data, error } = await $fetch.GET("/api/getTypeofUser", {
      params: {
        query: {
          user_id,
          speech_id: s_id,
        },
      },
    });
    if (error || data === -1) {
      console.log(error);
      messageApi.error("fetch role error");
    } else {
      setUserRole(data);
    }
  }

  useEffect(() => {
    fetchRole();
  }, [user_id, speech_id]);

  return (
    <HomePageLayout
      children={
        !speech_id || isUnknown ? (
          <ErrorBack />
        ) : (
          <>
            {contextHolder}
            {isOrganizerOrSpeaker && <TeacherSpeechPage />}
            {isStudent && <StudentSpeechPage />}
          </>
        )
      }
    />
  );
}

function TeacherSpeechPage() {
  const { speech_id } = useParams();
  const s_id = parseInt(speech_id ? speech_id : "-1");
  const user_id = useAtomValue(user_id_atom);
  const [userRole, setUserRole] = useState(-1);
  const isStudent = userRole === 1;
  const isOrganizerOrSpeaker = userRole === 0;
  const isUnknown = userRole === -1;
  const [speech, setSpeech] = useState<speech_data>();
  const [questions, setQuestions] = useState<question_data[]>();
  const [messageApi, contextHolder] = message.useMessage();
  const [comments, setComments] = useState<comment_data[]>([]);

  async function fetchRole() {
    const { data, error } = await $fetch.GET("/api/getTypeofUser", {
      params: {
        query: {
          user_id,
          speech_id: s_id,
        },
      },
    });
    if (error) {
      console.log(error);
      messageApi.error("获取用户信息失败");
    } else {
      setUserRole(data);
    }
  }

  async function fetchSpeech() {
    const speech_id = s_id;
    const { data, error } = await $fetch.GET("/api/getSpeechById", {
      params: {
        query: {
          speech_id: speech_id,
        },
      },
    });
    if (error) {
      console.log(error);
      messageApi.error("获取演讲失败");
    } else {
      setSpeech(data);
    }
  }

  async function fetchComment() {
    const speech_id = s_id;
    const { data, error } = await $fetch.GET("/api/getSpeAllComments", {
      params: {
        query: {
          speech_id,
        },
      },
    });
    if (error) {
      console.log(error);
      messageApi.error("获取评论失败");
    } else {
      setComments(data);
    }
  }

  async function fetchQuestions() {
    const speech_id = s_id;
    const { data, error } = await $fetch.GET("/api/getQuestionList", {
      params: {
        query: {
          speech_id,
        },
      },
    });
    if (error) {
      console.log(error);
      messageApi.success("获取测验列表出错");
    } else {
      setQuestions(data);
    }
  }

  useEffect(() => {
    fetchRole();
    fetchSpeech();
    fetchQuestions();
    fetchComment();
  }, [user_id, speech_id]);

  return (
    <>
      {s_id < 0 || isUnknown ? (
        <ErrorBack />
      ) : (
        <>
          {contextHolder}
          {speech && (
            <>
              <Typography.Title level={2}>
                演讲：{speech.title}
              </Typography.Title>
              <Typography>演讲ID：{speech.id}</Typography>
              <Typography>所属课程ID：{speech.course_id}</Typography>
              <Typography>演讲者ID：{speech.speaker_id}</Typography>
              {speech.speaker_id === user_id && (
                <>
                  <Divider />
                  <>FILELIST</>
                  <Upload
                    name={"file"}
                    action={"/api/upload"}
                    accept={".pdf,.ppt,.pptx"}
                    method={"POST"}
                    data={{
                      speech_id: s_id,
                    }}
                  >
                    <Button>
                      <UploadOutlined />
                      点击上传文档
                    </Button>
                  </Upload>
                  <Divider />
                  <Recorder speech_id={speech.id} />
                </>
              )}
              <Divider />
              <Typography.Title level={2}>小测验列表：</Typography.Title>
              <ModalForm<{
                question: string;
                selection: string;
                answer: string;
                speech_id: number;
                start_time: number;
                end_time: number;
              }>
                title={"手动新建题目"}
                trigger={<Button>手动添加题目</Button>}
                onFinish={async (formData) => {
                  console.log(formData);
                  const { data, error } = await $fetch.POST(
                    "/api/createQuestion",
                    {
                      body: {
                        ...formData,
                        start_time: dayjs(formData.start_time).toISOString(),
                        end_time: dayjs(formData.end_time).toISOString(),
                      },
                    },
                  );
                  if (error || data === -1) {
                    console.log(error);
                    messageApi.error("新建题目失败");
                  } else {
                    messageApi.success("新建题目成功");
                    await fetchQuestions();
                  }
                }}
              >
                <ProFormText name={"question"} label={"题目"} />
                <ProFormTextArea
                  name={"selection"}
                  label={"选项：一个选项一行，每行第一个字符是答案"}
                />
                <ProFormText name={"answer"} label={"答案：一个字符"} />
                <ProFormDigit
                  name={"speech_id"}
                  label={"所属演讲ID"}
                  initialValue={s_id}
                />
                <ProFormTimePicker
                  name={"start_time"}
                  label={"开始时间"}
                  initialValue={Date.now()}
                />
                <ProFormTimePicker
                  name={"end_time"}
                  label={"结束时间"}
                  initialValue={Date.now() + 600 * 1000}
                />
              </ModalForm>
              <ModalForm<{
                speech_id: number;
                start_time: string;
                end_time: string;
              }>
                title={"AI生成题目"}
                trigger={<Button>AI生成题目</Button>}
                onFinish={async (formData) => {
                  console.log(formData);
                  const { data, error } = await $fetch.GET("/api/generateQue", {
                    params: {
                      query: {
                        speech_id: s_id,
                      },
                    },
                  });
                  if (error || !data) {
                    console.log(error);
                    messageApi.error("AI生成题目出错");
                  } else {
                    messageApi.success(`AI生成题目成功，题目ID：${data.id}`);
                    await fetchQuestions();
                  }
                }}
              >
                <ProFormDigit
                  name={"speech_id"}
                  label={"所属演讲ID"}
                  initialValue={s_id}
                />
                <ProFormTimePicker
                  name={"start_time"}
                  label={"开始时间"}
                  initialValue={Date.now()}
                />
                <ProFormTimePicker
                  name={"end_time"}
                  label={"结束时间"}
                  initialValue={Date.now() + 600 * 1000}
                />
              </ModalForm>
              <List
                dataSource={questions}
                renderItem={(item) => (
                  <QuestionListItem
                    data={item}
                    is_teacher={isOrganizerOrSpeaker}
                  />
                )}
              />
              <Divider />
              <CommentList
                dataSource={comments}
                onSubmit={async (value) => {
                  const speech_id = s_id;
                  const { data, error } = await $fetch.POST(
                    "/api/addSpeComment",
                    {
                      body: {
                        speech_id,
                        user_id,
                        comment: value,
                      },
                    },
                  );
                  if (error || data === -1) {
                    messageApi.error("发表评论失败");
                    return;
                  } else {
                    messageApi.success(`发表评论成功`);
                    await fetchComment();
                  }
                }}
              />
            </>
          )}
        </>
      )}
    </>
  );
}

function StudentSpeechPage() {
  const { speech_id } = useParams();
  const s_id = parseInt(speech_id ? speech_id : "-1");
  const user_id = useAtomValue(user_id_atom);
  const [userRole, setUserRole] = useState(-1);
  const isStudent = userRole === 1;
  const isOrganizerOrSpeaker = userRole === 0;
  const isUnknown = userRole === -1;
  const [speech, setSpeech] = useState<speech_data>();
  const [questions, setQuestions] = useState<question_data[]>();
  const [messageApi, contextHolder] = message.useMessage();
  const [comments, setComments] = useState<comment_data[]>([]);
  const [numOfRight, setNumOfRight] = useState<number>(0);
  const [rateOfMine, setRateOfMine] = useState<number>(0.0);
  const [rankOfMine, setRankOfMine] = useState<number>(1);

  async function fetchRole() {
    const { data, error } = await $fetch.GET("/api/getTypeofUser", {
      params: {
        query: {
          user_id,
          speech_id: s_id,
        },
      },
    });
    if (error) {
      console.log(error);
      messageApi.error("获取用户信息失败");
    } else {
      setUserRole(data);
    }
  }

  async function fetchSpeech() {
    const speech_id = s_id;
    const { data, error } = await $fetch.GET("/api/getSpeechById", {
      params: {
        query: {
          speech_id: speech_id,
        },
      },
    });
    if (error) {
      console.log(error);
      messageApi.error("获取演讲失败");
    } else {
      setSpeech(data);
    }
  }

  async function fetchComment() {
    const speech_id = s_id;
    const { data, error } = await $fetch.GET("/api/getSpeAllComments", {
      params: {
        query: {
          speech_id,
        },
      },
    });
    if (error) {
      console.log(error);
      messageApi.error("获取评论失败");
    } else {
      setComments(data);
    }
  }

  async function fetchQuestions() {
    const speech_id = s_id;
    const { data, error } = await $fetch.GET("/api/getQuestionList", {
      params: {
        query: {
          speech_id,
        },
      },
    });
    if (error) {
      console.log(error);
      messageApi.success("获取测验列表出错");
    } else {
      setQuestions(data);
    }
  }

  async function fetchStatics() {
    const res1 = await $fetch.GET("/api/getNumofRight", {
      params: {
        query: {
          speech_id: s_id,
          user_id,
        },
      },
    });
    if (res1.error) {
      messageApi.error("获取统计数据失败");
    } else {
      setNumOfRight(res1.data);
    }
    const res2 = await $fetch.GET("/api/getRateofMine", {
      params: {
        query: {
          speech_id: s_id,
          user_id,
        },
      },
    });
    if (res2.error) {
      messageApi.error("获取统计数据失败");
    } else {
      setRateOfMine(res2.data);
    }
    const res3 = await $fetch.GET("/api/getRankofMine", {
      params: {
        query: { speech_id: s_id, user_id },
      },
    });
    if (res3.error) {
      messageApi.error("获取统计数据失败");
    } else {
      setRankOfMine(res3.data);
    }
  }

  useEffect(() => {
    const intervalId = speech && setInterval(fetchQuestions, 30_000);
    return () => clearInterval(intervalId);
  }, [speech]);

  useEffect(() => {
    fetchRole();
    fetchSpeech();
    fetchQuestions();
    fetchComment();
    fetchStatics();
  }, [user_id, speech_id]);
  return (
    <>
      {contextHolder}
      {speech && (
        <>
          <Typography.Title level={2}>演讲：{speech.title}</Typography.Title>
          <Typography>演讲ID：{speech.id}</Typography>
          <Typography>所属课程ID：{speech.course_id}</Typography>
          <Typography>演讲者ID：{speech.speaker_id}</Typography>
          <Divider />
          <Typography.Title level={2}>我的小测验统计数据：</Typography.Title>
          <Space size={"large"}>
            <Typography>一共作对了{numOfRight}题</Typography>
            <Typography>正确率：{rateOfMine * 100}%</Typography>
            <Typography>正确率排名：第{rankOfMine}名</Typography>
          </Space>
          <Divider />
          <Typography.Title level={2}>小测验列表：</Typography.Title>
          <List
            dataSource={questions}
            renderItem={(item) => (
              <QuestionListItem data={item} is_teacher={false} />
            )}
          />
          <Divider />
          <CommentList
            dataSource={comments}
            onSubmit={async (value) => {
              const speech_id = s_id;
              const { data, error } = await $fetch.POST("/api/addSpeComment", {
                body: {
                  speech_id,
                  user_id,
                  comment: value,
                },
              });
              if (error || data === -1) {
                messageApi.error("发表评论失败");
                return;
              } else {
                messageApi.success(`发表评论成功`);
                await fetchComment();
              }
            }}
          />
        </>
      )}
    </>
  );
}
