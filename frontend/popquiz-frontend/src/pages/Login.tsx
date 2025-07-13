import {LockOutlined, UserOutlined,} from '@ant-design/icons';
import {LoginForm, ProConfigProvider, ProForm, ProFormCheckbox, ProFormText,} from '@ant-design/pro-components';
import {Flex, message, theme, Typography} from 'antd';
import {$fetch} from "../api/api-utils.ts";
import {useSetAtom} from "jotai";
import {user_id_atom} from "../states/user.ts";
import {Link} from "react-router";

export function LoginPage() {
    const {token} = theme.useToken();
    const [messageApi, contextHolder] = message.useMessage()
    const setUserId = useSetAtom(user_id_atom);
    const onFinish = async ({username, password}: {
        username: string,
        password: string,
    }) => {
        const {data, error} = await $fetch.POST('/api/login', {
            body: {
                username,
                password
            }
        })
        if (error) {
            console.log(error);
            messageApi.error('登录失败，检查你的用户名和密码')
            setUserId(-1);
        } else {
            console.log(data);
            messageApi.success('登录成功')
            setUserId(data);
        }
    }
    return (
        <ProConfigProvider hashed={false}>
            {contextHolder}
            <div style={{backgroundColor: token.colorBgContainer}}>
                <LoginForm<{
                    username: string,
                    password: string,
                }>
                    logo="https://github.githubassets.com/favicons/favicon.png"
                    title="PopQuiz"
                    subTitle="全球最大的PopQuiz平台"
                    onFinish={onFinish}
                >
                    <>
                        <ProFormText
                            name="username"
                            fieldProps={{
                                size: 'large',
                                prefix: <UserOutlined className={'prefixIcon'}/>,
                            }}
                            placeholder={'用户名'}
                            rules={[
                                {
                                    required: true,
                                    message: '请输入用户名!',
                                },
                            ]}
                        />
                        <ProFormText.Password
                            name="password"
                            fieldProps={{
                                size: 'large',
                                prefix: <LockOutlined className={'prefixIcon'}/>
                            }}
                            placeholder={'密码'}
                            rules={[
                                {
                                    required: true,
                                    message: '请输入密码！',
                                },
                            ]}
                        />
                    </>

                    <div
                        style={{
                            marginBlockEnd: 24,
                        }}
                    >
                        <ProFormCheckbox noStyle name="autoLogin">
                            自动登录
                        </ProFormCheckbox>
                        <Link style={{
                            float: 'right',
                        }} to={'/signup'}>没有账号？注册一个</Link>

                    </div>
                </LoginForm>
            </div>
        </ProConfigProvider>
    );
}

export function SignupPage() {
    const [messageApi, contextHolder] = message.useMessage()
    return <Flex style={{width: '100%'}} justify={'center'}>
        <ProForm<{ username: string, password: string, name: string, password2: string }>
            style={{
                width: '328px',
            }}
            onFinish={async ({username, password, name, password2}) => {
                if (password !== password2) {
                    messageApi.error('两次密码输入不一致！')
                    return;
                }
                const {error} = await $fetch.POST('/api/register', {
                    body: {
                        username,
                        name,
                        password
                    }
                })
                if (error) {
                    messageApi.error('注册失败');
                } else {
                    messageApi.success('注册成功');
                }
            }}
            submitter={{
                resetButtonProps: {style: {display: 'none'}}, submitButtonProps: {
                    block: true
                }
            }}
        >
            {contextHolder}
            <Typography.Title>
                注册账号
            </Typography.Title>
            <Link style={{}}
                  to={'/'}>
                已有账号？点此登录
            </Link>
            <>

                <ProFormText
                    name="name"
                    fieldProps={{
                        size: 'large',
                        prefix: <UserOutlined className={'prefixIcon'}/>,
                    }}
                    placeholder={'昵称'}
                    rules={[
                        {
                            required: true,
                            message: '请输入你的昵称!',
                        },
                    ]}
                />
                <ProFormText
                    name="username"
                    fieldProps={{
                        size: 'large',
                        prefix: <UserOutlined className={'prefixIcon'}/>,
                    }}
                    placeholder={'用户名'}
                    rules={[
                        {
                            required: true,
                            message: '请输入用户名!',
                        },
                    ]}
                />
                <ProFormText.Password
                    name="password"
                    fieldProps={{
                        size: 'large',
                        prefix: <LockOutlined className={'prefixIcon'}/>
                    }}
                    placeholder={'密码'}
                    rules={[
                        {
                            required: true,
                            message: '请输入密码！',
                        },
                    ]}
                />
                <ProFormText.Password
                    name="password2"
                    fieldProps={{
                        size: 'large',
                        prefix: <LockOutlined className={'prefixIcon'}/>
                    }}
                    placeholder={'再次输入密码'}
                    rules={[
                        {
                            required: true,
                            message: '请再次输入密码！',
                        },
                    ]}
                />
            </>
        </ProForm>
    </Flex>
}