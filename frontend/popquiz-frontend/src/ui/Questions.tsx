import {Button, List, message, Radio, Space, Typography} from "antd";
import {useEffect, useState} from "react";
import dayjs from "dayjs";
import {Link} from "react-router";
import {useAtomValue} from "jotai";
import {user_id_atom} from "../states/user.ts";
import {$fetch} from "../api/api-utils.ts";

export type question_data = {
    id: number,
    question: string,
    selection: string,
    answer: string,
    speech_id: number,
    start_time: string,
    end_time: string,
}

const test_data: question_data[] = [
    {
        id: 1,
        question: '1+1=?',
        selection: 'A.1\nB.2\nC.3\nD.4',
        answer: 'B',
        speech_id: 1,
        start_time: new Date().toLocaleTimeString(),
        end_time: new Date(Date.now() + 60 * 60 * 1000).toLocaleTimeString(),
    }
]

export function QuestionListItem({data, is_teacher}: { data: question_data, is_teacher: boolean }) {
    return (
        <List.Item>
            <List.Item.Meta
                title={data.question}
                description={<>
                    <div>
                        开始作答时间：{dayjs(data.start_time).format('YYYY-MM-DD HH:mm:ss')}
                    </div>
                    <div>
                        截止作答时间：{dayjs(data.end_time).format('YYYY-MM-DD HH:mm:ss')}
                    </div>
                </>}
            />
            <Space size="middle">
                <Link to={`/question/${is_teacher ? 'teacher' : 'student'}/${data.id}`}>查看</Link>
            </Space>
        </List.Item>
    )
}

export function QuestionList({speech_id, is_teacher}: { speech_id: number, is_teacher: boolean }) {
    const [questions, setQuestions] = useState<question_data[]>(test_data);
    const [messageApi, contextHolder] = message.useMessage()

    async function fetchQuestions() {
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
        fetchQuestions()
    }, [speech_id]);

    return <>
        {contextHolder}
        <Typography.Title level={2}>小测验列表：</Typography.Title>
        <List
            dataSource={questions}
            renderItem={(item) => <QuestionListItem data={item} is_teacher={is_teacher}/>}
        />
    </>
}

export function QuestionShowcase({question_id}: { question_id: number }) {
    const [question, setQuestion] = useState<question_data>(test_data[0]);
    const [selected, setSelected] = useState<string>('')
    const [submitted, setSubmitted] = useState<boolean>(false);
    const user_id = useAtomValue(user_id_atom);
    const [messageApi, contextHolder] = message.useMessage()

    async function fetchQuestion() {
        const {data, error} = await $fetch.GET('/api/getQuestionById', {
            params: {
                query: {
                    question_id
                }
            }
        });
        if (error) {
            messageApi.error('获取题目出错');
        } else {
            setQuestion(data);
        }
        fetchSubmittion()
    }

    async function fetchSubmittion() {
        const {data, error} = await $fetch.GET('/api/getSubmitId', {
            params: {
                query: {
                    quiz_id: question_id,
                    user_id: user_id,
                }
            }
        });
        if (error || data === -1) {
            messageApi.error('获取作答失败');
            // logic for time out
            if (dayjs(question.end_time).isBefore(dayjs())) {
                setSubmitted(true);
            }
            return
        }
        const submit_id = data;
        const res2 = await $fetch.GET('/api/getSubmitById', {
            params: {
                query: {
                    submit_id
                }
            }
        })
        if (res2.error || res2.data === undefined) {
            messageApi.error('获取作答失败！');
            return
        }
        if (res2.data.answer.length > 0) {
            setSelected(res2.data.answer);
            setSubmitted(true);
        }
    }

    async function submitQuestion() {
        const {data, error} = await $fetch.POST('/api/createSubmit', {
            body: {
                question_id,
                user_id,
                answer: selected
            }
        })
        if (error || data === -1) {
            messageApi.error('提交作答失败');
            return
        }
        messageApi.success('提交成功');
        setSubmitted(true);
    }

    useEffect(() => {
        fetchQuestion()
    }, [question_id, user_id])


    return (<>
        {contextHolder}
        <Typography.Title level={2}>小测验：</Typography.Title>
        <Typography.Title level={4}>题目：{question.question}</Typography.Title>
        <div>
            开始作答时间：{dayjs(question.start_time).format('YYYY-MM-DD HH:mm:ss')}
        </div>
        <div>
            截止作答时间：{dayjs(question.end_time).format('YYYY-MM-DD HH:mm:ss')}
        </div>
        <Typography.Title level={4}>选项：</Typography.Title>
        <Radio.Group
            style={{display: 'flex', flexDirection: 'column', gap: '8',}}
            onChange={(e) => {
                if (!submitted) {
                    setSelected(e.target.value)
                }
            }}
            value={selected}
            options={question.selection.split('\n', 4).map(answer => ({
                value: answer[0], label: answer
            }))}
        />
        {submitted && <Typography.Title level={5}>正确答案:{question.answer}<br/>你的答案:{selected}</Typography.Title>}
        <Button type={'primary'} onClick={submitQuestion} disabled={submitted}>提交</Button>
    </>)
}
