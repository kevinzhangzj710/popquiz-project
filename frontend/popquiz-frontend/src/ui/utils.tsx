import {useNavigate} from "react-router";
import {Button, Typography} from "antd";

export function ErrorBack() {
    const navi = useNavigate();
    return <>
        <Typography>404，你访问的页面不存在，返回上一页吧。</Typography>
        <Button type={'link'} onClick={() => navi(-1)}>点此返回</Button>
    </>
}