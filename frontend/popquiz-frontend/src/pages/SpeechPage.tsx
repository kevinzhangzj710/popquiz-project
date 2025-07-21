import {useParams} from "react-router";
import {HomePageLayout} from "./Dashboard.tsx";
import {ErrorBack} from "../ui/utils.tsx";
import {type speech_data} from "../ui/Speeches.tsx";
import {useAtomValue} from "jotai/index";
import {user_id_atom} from "../states/user.ts";
import {useEffect, useState} from "react";
import {Divider, List, message, Typography} from "antd";
import {type comment_data, CommentList} from "../ui/Comments.tsx";
import {$fetch} from "../api/api-utils.ts";
import {Recorder} from "../ui/Recorder.tsx";
import {type question_data, QuestionListItem} from "../ui/Questions.tsx";

export function SpeechPage() {
    const {speech_id} = useParams();
    const s_id = parseInt(speech_id ? speech_id : '-1');
    const user_id = useAtomValue(user_id_atom);
    const [userRole, setUserRole] = useState(-1);
    const isStudent = userRole === 1;
    const isOrganizerOrSpeaker = userRole === 0;
    const isUnknown = userRole === -1;
    const [speech, setSpeech] = useState<speech_data>();
    const [questions, setQuestions] = useState<question_data[]>();
    const [messageApi, contextHolder] = message.useMessage()
    const [comments, setComments] = useState<comment_data[]>([]);

    async function fetchRole() {
        // TODO lack of API
        setUserRole(0);// just for debug
    }

    async function fetchSpeech() {
        const speech_id = s_id;
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
        const speech_id = s_id;
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

    async function fetchQuestions() {
        const speech_id = s_id;
        const {data, error} = await $fetch.GET('/api/getQuestionList', {
            params: {
                query: {
                    speech_id
                }
            }
        })
        if (error) {
            console.log(error);
            messageApi.success('获取测验列表出错')
        } else {
            setQuestions(data);
        }
    }

    useEffect(() => {
        fetchRole()
        fetchSpeech()
        fetchQuestions()
        fetchComment()
    }, [user_id, speech_id]);

    return <HomePageLayout children={!speech_id || isUnknown ? <ErrorBack/> :
        <>
            {contextHolder}
            {speech && <>
                <Typography.Title level={2}>演讲：{speech.title}</Typography.Title>
                <Typography>演讲ID：{speech.id}</Typography>
                <Typography>所属课程ID：{speech.course_id}</Typography>
                <Typography>演讲者ID：{speech.speaker_id}</Typography>
                {speech.speaker_id === user_id && <><Divider/><Recorder speech_id={speech.id}/></>}
                <Divider/>
                <Typography.Title level={2}>小测验列表：</Typography.Title>
                <List
                    dataSource={questions}
                    renderItem={(item) => <QuestionListItem data={item} is_teacher={isOrganizerOrSpeaker}/>}
                />
                <Divider/>
                <CommentList dataSource={comments} onSubmit={async (value) => {
                    const speech_id = s_id;
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
            </>}
        </>}/>
}