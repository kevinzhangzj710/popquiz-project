import { Flex, Layout, Menu, theme } from "antd";
import { Content, Footer, Header } from "antd/es/layout/layout";
import { CourseList } from "../ui/Courses.tsx";
import { Link } from "react-router";
import { useSetAtom } from "jotai";
import { user_id_atom } from "../states/user.ts";

export function HomePageLayout({ children }: { children: React.ReactNode }) {
    const {
        token: { colorBgContainer, borderRadiusLG },
    } = theme.useToken();
    const setUserId = useSetAtom(user_id_atom)
    return (<>
        <Layout>
            <Header>
                <Menu theme={'dark'} mode={'horizontal'} style={{ flex: 1, minWidth: 0 }}
                    defaultSelectedKeys={['1']}
                    items={[{ key: 1, label: <Link to={'/'}>主页</Link>, }, {
                        key: 2, label: <Link to={'/'} onClick={() => {
                            setUserId(-1);
                        }}>退出登录</Link>
                    }]}
                />
            </Header>
            <Content style={{ padding: '48px' }}>
                <Flex justify={'center'}>
                    <div style={{
                        background: colorBgContainer, minHeight: 280,
                        padding: 24,
                        borderRadius: borderRadiusLG,
                        minWidth: "800",
                        flex: '0.8'
                    }}>
                        {children}
                    </div>
                </Flex>

            </Content>
            <Footer style={{ textAlign: 'center' }}>
                Popquiz Project
            </Footer>
        </Layout>
    </>)
}

export function HomePage() {

    return (<>
        <HomePageLayout children={<>
            <CourseList teaching={false} />
            <CourseList teaching={true} />
        </>} />

    </>)
}