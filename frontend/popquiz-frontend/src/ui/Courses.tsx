import React, { useEffect, useState } from "react";
import { Button, List, message, Popconfirm, Space, Typography } from "antd";
import { Link, useNavigate } from "react-router";
import { useAtomValue } from "jotai";
import { user_id_atom } from "../states/user.ts";
import { SpeechList } from "./Speeches.tsx";
import { $fetch } from "../api/api-utils.ts";
import {
  ModalForm,
  ProFormCheckbox,
  ProFormDigit,
  ProFormText,
} from "@ant-design/pro-components";

export type course_data = {
  id: number;
  title: string;
  description: string;
  organizer_id: number;
};

export function CourseListItem({
  data,
  extra,
}: {
  data: course_data;
  extra?: React.ReactNode;
}) {
  return (
    <List.Item>
      <List.Item.Meta
        title={data.title}
        description={
          <>
            <div>组织者：{data.organizer_id}</div>
            <div>简介：{data.description}</div>
          </>
        }
      />
      <Space size={"middle"}>
        <Link to={`/course/${data.id}`}>查看</Link>
        {extra}
      </Space>
    </List.Item>
  );
}

export function CourseList({
  teaching,
}: {
  teaching: boolean;
  renderer?: (data: course_data) => React.ReactNode;
}) {
  const [courses, setCourses] = useState<course_data[]>([]);
  const user_id = useAtomValue(user_id_atom);
  const [messageApi, contextHolder] = message.useMessage();

  // api fetching
  async function fetchCourses() {
    if (teaching) {
      // teaching a class api
      const { data, error } = await $fetch.GET("/api/getTeachingCourse", {
        params: {
          query: {
            uid: user_id,
          },
        },
      });
      if (error) {
        console.log(error);
        messageApi.error("获取课程失败");
      } else {
        setCourses(data);
      }
    } else {
      // listening a class api
      const { data, error } = await $fetch.GET("/api/getListeningCourse", {
        params: {
          query: {
            uid: user_id,
          },
        },
      });
      if (error) {
        console.log(error);
        messageApi.error("获取课程失败");
      } else {
        setCourses(data);
      }
    }
  }

  useEffect(() => {
    fetchCourses();
  }, [user_id, teaching]);
  return (
    <>
      {contextHolder}
      <Typography.Title level={2}>
        我{teaching ? "教" : "听"}的课：
      </Typography.Title>
      {teaching && (
        <ModalForm<{ title: string; description: string; organizer_id: number }>
          title={"新建课程"}
          trigger={<Button>新建课程</Button>}
          onFinish={async (formData) => {
            const { data, error } = await $fetch.POST("/api/createCourse", {
              body: {
                ...formData,
              },
            });
            if (error || data === -1) {
              console.log(error);
              messageApi.error("创建课程失败");
            } else {
              messageApi.success(`创建课程成功，课程ID：${data}`);
              await fetchCourses();
            }
          }}
        >
          <ProFormText name={"title"} label={"课程标题"} />
          <ProFormText name={"description"} label={"课程简介"} />
          <ProFormDigit
            name={"organizer_id"}
            label={"课程组织者ID"}
            initialValue={user_id}
          />
        </ModalForm>
      )}
      <ModalForm<{
        course_id: number;
        is_speaker: boolean;
      }>
        title={"加入课程"}
        trigger={<Button>加入课程</Button>}
        onFinish={async (formData) => {
          if (formData.is_speaker) {
            // TODO lack of API
          } else {
            const { data, error } = await $fetch.POST("/api/addCourse", {
              body: {
                uid: user_id,
                course_id: formData.course_id,
              },
            });
            if (error || data === 0) {
              messageApi.error("添加课程失败");
            } else {
              messageApi.success(`添加课程成功`);
              await fetchCourses();
            }
          }
        }}
      >
        <ProFormDigit name={"course_id"} label={"课程ID"} />
        <ProFormCheckbox
          disabled={!teaching}
          name={"is_student"}
          label={"以演讲者身份加入"}
          initialValue={teaching}
        />
      </ModalForm>

      <List
        dataSource={courses}
        renderItem={(item) =>
          teaching ? (
            <CourseListItem data={item} />
          ) : (
            <CourseListItem
              data={item}
              extra={
                <Popconfirm
                  title={"确定退出这个课程吗？"}
                  description={
                    <Typography>
                      <Typography>课程名：{item.title}</Typography>
                      <Typography>组织者ID：{item.organizer_id}</Typography>
                    </Typography>
                  }
                  onConfirm={async () => {
                    const { error } = await $fetch.POST(
                      "/api/deleteCourse_Listen",
                      {
                        body: {
                          uid: user_id,
                          course_id: item.id,
                        },
                      },
                    );
                    if (error) {
                      console.log(error);
                      messageApi.error("退出课程失败");
                    } else {
                      messageApi.success("退出课程成功");
                      await fetchCourses();
                    }
                  }}
                >
                  <Button type={"link"}>退出</Button>
                </Popconfirm>
              }
            />
          )
        }
      />
    </>
  );
}

export function CourseShowcase({ course_id }: { course_id: number }) {
  const [course, setCourse] = useState<course_data>();
  const user_id = useAtomValue(user_id_atom);
  const navi = useNavigate();
  const [messageApi, contextHolder] = message.useMessage();

  async function fetchCourse() {
    const { data, error } = await $fetch.GET("/api/getCourseById", {
      params: {
        query: {
          course_id: course_id,
        },
      },
    });
    if (error) {
      console.log(error);
      messageApi.error("获取课程失败");
    } else {
      setCourse(data);
    }
  }

  useEffect(() => {
    fetchCourse();
  }, [course_id, user_id]);

  return (
    <>
      {contextHolder}
      {!course && (
        <a
          onClick={() => {
            navi(-1);
          }}
        >
          发生错误，请返回
        </a>
      )}
      {course && (
        <>
          <Typography.Title level={2}>课程：{course.title}</Typography.Title>
          <Typography>课程ID：{course.organizer_id}</Typography>
          <Typography>组织者：{course.organizer_id}</Typography>
          <Typography>简介：{course.description}</Typography>
          <SpeechList
            course_id={course_id}
            is_organizer={course.organizer_id === user_id}
          />
        </>
      )}
    </>
  );
}
