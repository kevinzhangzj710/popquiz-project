import {Button, Divider, List, message, Space, Typography} from "antd";
import {Link} from "react-router";
import * as React from "react";
import {useEffect, useState} from "react";
import {useAtomValue} from "jotai/index";
import {user_id_atom} from "../states/user.ts";
import {ErrorBack} from "./utils.tsx";
import {$fetch} from "../api/api-utils.ts";
import {QuestionList} from "./Questions.tsx";
import {Recorder} from "./Recorder.tsx";
import {ModalForm, ProFormDigit, ProFormText} from "@ant-design/pro-components";
import {type comment_data, CommentList} from "./Comments.tsx";

export type speech_data = { id: number, title: string, speaker_id: number, course_id: number }

const test_data: speech_data[] = [
    {id: 1, title: 't1', speaker_id: 1, course_id: 2},
    {id: 2, title: 't1', speaker_id: 1, course_id: 2},
    {id: 3, title: 't1', speaker_id: 1, course_id: 2},
]

export function SpeechListItemD({data, renderLink}: {
    data: speech_data,
    renderLink: (d: speech_data) => React.ReactNode
}) {
    return (<List.Item
    >
        <List.Item.Meta
            title={data.title}
            description={<>
                <div>
                    演讲者：{data.speaker_id}
                </div>
            </>}/>
        <Space size={'middle'}>
            {renderLink(data)}
            <Link to={`/speech/${data.id}`}>查看</Link>
        </Space>
    </List.Item>)
}

export function SpeechListItem({data}: { data: speech_data }) {
    return (<List.Item
    >
        <List.Item.Meta
            title={data.title}
            description={<>
                <div>
                    演讲者：{data.speaker_id}
                </div>
            </>}/>
        <Space size={'middle'}>
            <Link to={`/speech/${data.id}`}>查看</Link>
        </Space>
    </List.Item>)
}

export function SpeechList({course_id, is_organizer}: { course_id: number, is_organizer: boolean }) {
    const [speeches, setSpeeches] = useState<speech_data[]>();
    const user_id = useAtomValue(user_id_atom);
    const [messageApi, contextHolder] = message.useMessage()

    async function fetchSpeeches() {
        // course_id ,user_id
        const {data, error} = await $fetch.GET('/api/getSpeeches', {
            params: {
                query: {
                    courseid: course_id
                }
            }
        })
        if (error) {
            console.log(error)
            messageApi.error('获取课程演讲列表失败')
        } else {
            setSpeeches(data)
        }
    }

    useEffect(() => {
        fetchSpeeches()
    }, [user_id, course_id]);

    return (<>
        {contextHolder}
        <Typography.Title level={2}>
            演讲列表：
        </Typography.Title>
        {is_organizer &&
            <ModalForm<{ title: string, speaker_id: number, course_id: number }>
                title={'新建演讲'}
                trigger={<Button>新建演讲</Button>}
                onFinish={async (formData) => {
                    const {data, error} = await $fetch.POST('/api/createSpeech', {
                        body: {
                            ...formData
                        }
                    })
                    if (error || data === -1) {
                        console.log(error)
                        messageApi.error('创建演讲失败');
                    } else {
                        messageApi.success(`创建演讲成功，演讲ID：${data}`)
                        await fetchSpeeches();
                    }
                }}
            >
                <ProFormText
                    name={'title'}
                    label={'标题'}
                />
                <ProFormDigit
                    name={'speaker_id'}
                    label={'演讲者ID'}
                    initialValue={user_id}
                />
                <ProFormDigit
                    name={'course_id'}
                    label={'所属课程ID'}
                    initialValue={course_id}
                />
            </ModalForm>
        }
        <List
            dataSource={speeches}
            renderItem={(item) => <SpeechListItem data={item}/>}
        />
    </>)
}

export function SpeechShowcase({speech_id}: { speech_id: number }) {
    const [speech, setSpeech] = useState<speech_data>();
    const user_id = useAtomValue(user_id_atom);
    const [messageApi, contextHolder] = message.useMessage()
    const [comments, setComments] = useState<comment_data[]>([]);

    async function fetchSpeech() {
        const {data, error} = await $fetch.GET('/api/getSpeechById', {
            params: {
                query: {
                    speech_id: speech_id
                }
            }
        })
        if (error) {
            console.log(error)
            messageApi.error('获取演讲失败')
        } else {
            setSpeech(data)
        }
    }

    async function fetchComment() {
        const {data, error} = await $fetch.GET('/api/getSpeAllComments', {
            params: {
                query: {
                    speech_id
                }
            }
        })
        if (error) {
            console.log(error)
            messageApi.error('获取评论失败')
        } else {
            setComments(data)
        }
    }

    useEffect(() => {
        fetchSpeech()
        fetchComment()
    }, [speech_id, user_id]);

    return <>
        {contextHolder}
        {!speech && <ErrorBack/>}
        {speech && <>
            <Typography.Title level={2}>演讲：{speech.title}</Typography.Title>
            <Typography>演讲ID：{speech.id}</Typography>
            <Typography>所属课程ID：{speech.course_id}</Typography>
            <Typography>演讲者ID：{speech.speaker_id}</Typography>

            {speech.speaker_id === user_id && <><Divider/><Recorder speech_id={speech.id}/></>}
            <Divider/>
            <QuestionList speech_id={speech.id} is_teacher={true}/>
            <CommentList dataSource={comments} onSubmit={async (value) => {
                const {data, error} = await $fetch.POST('/api/addSpeComment', {
                    body: {
                        speech_id,
                        user_id,
                        comment: value
                    }
                })
                if (error || data === -1) {
                    messageApi.error('发表评论失败');
                    return
                } else {
                    messageApi.success(`发表评论成功`)
                    await fetchComment()
                }
            }}/>
        </>
        }
    </>
}