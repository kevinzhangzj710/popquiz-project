import { Button, Flex, Input, List, Typography } from "antd";
import { useState } from "react";

export type comment_data = {
  id: number;
  user_id: number;
  comment: string;
};

export function CommentList({
  dataSource,
  onSubmit,
}: {
  dataSource: comment_data[];
  onSubmit: (value: string) => Promise<void>;
}) {
  const [myComment, setMyComment] = useState("");
  return (
    <>
      <Typography.Title level={2}>评论区：</Typography.Title>
      <List
        dataSource={dataSource}
        renderItem={(item) => (
          <>
            <List.Item>
              <List.Item.Meta title={item.comment} />
            </List.Item>
          </>
        )}
      />
      <Flex gap={"small"}>
        <Input
          value={myComment}
          onChange={(e) => setMyComment(e.target.value)}
        />
        <Button
          type={"primary"}
          onClick={async () => {
            await onSubmit(myComment);
            setMyComment("");
          }}
        >
          提交评论
        </Button>
      </Flex>
    </>
  );
}
