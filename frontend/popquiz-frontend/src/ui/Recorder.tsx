import { useEffect, useRef, useState } from "react";
import { Button, Card, message, Space, Typography } from "antd";
import { $fetch } from "../api/api-utils";

export function Recorder({ speech_id }: { speech_id: number }) {
  const [recording, setRecording] = useState<boolean>(false);
  const mediaRecorderRef = useRef<MediaRecorder>(undefined);
  const audioChunksRef = useRef<Blob[]>([]);
  const [convertedText, setConvertedText] = useState("");
  const [messageApi, contextHolder] = message.useMessage();

  const sendRecording = async (audioBlob: Blob) => {
    const formData = new FormData();
    formData.append("file", audioBlob, "example.webm");
    const params = new URLSearchParams({
      speech_id: speech_id.toString(),
    });
    fetch(`/api/tingwu/uploadVoiceFile?${params.toString()}`, {
      method: "POST",
      body: formData,
    }).catch((e) => {
      console.log(e);
      messageApi.error("上传实时录音失败");
    });
  };

  const startRecording = async () => {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    mediaRecorderRef.current = new MediaRecorder(stream);
    mediaRecorderRef.current.start(30_000);
    mediaRecorderRef.current.ondataavailable = (e) => {
      console.log(e.data);
      audioChunksRef.current.push(e.data);
      const audioBlob = new Blob([e.data], { type: "audio/wav" });
      sendRecording(audioBlob);
    };
    mediaRecorderRef.current.onstop = async () => {
      audioChunksRef.current = [];
    };

    setRecording(true);
  };
  const stopRecording = () => {
    mediaRecorderRef.current?.stop();
    setRecording(false);
  };
  const downloadWAV = (audioBlob: Blob) => {
    const url = URL.createObjectURL(audioBlob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "recording.wav";
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  };

  const fetchConvertedText = async () => {
    const { data, error } = await $fetch.GET("/api/tingwu/getTextofVoice", {
      params: {
        query: {
          speech_id,
        },
      },
    });
    if (error) {
      console.log(error);
      messageApi.error("获取转录文本失败");
    } else {
      setConvertedText(data);
    }
  };
  useEffect(() => {
    fetchConvertedText();
  }, [speech_id, audioChunksRef.current]);

  return (
    <Card title={"实时语音转录功能"}>
      {contextHolder}
      <Space size={"middle"}>
        <Typography
          style={{
            color: recording ? "red" : "black",
            fontWeight: "bold",
          }}
        >
          状态：{recording ? "录音中" : "停止"}
        </Typography>
        <Button
          type={"primary"}
          onClick={() => {
            if (recording) {
              stopRecording();
            } else {
              startRecording();
            }
          }}
        >
          {recording ? "结束录音" : "开始录音"}
        </Button>
      </Space>
      {/*<Divider />*/}
      {/*<Collapse*/}
      {/*  items={[*/}
      {/*    {*/}
      {/*      key: "1",*/}
      {/*      label: "点击查看实时转录内容",*/}
      {/*      children: (*/}
      {/*        <div className={"max-h-96 overflow-auto"}>*/}
      {/*          <Typography>{convertedText}</Typography>*/}
      {/*        </div>*/}
      {/*      ),*/}
      {/*    },*/}
      {/*  ]}*/}
      {/*/>*/}
    </Card>
  );
}
