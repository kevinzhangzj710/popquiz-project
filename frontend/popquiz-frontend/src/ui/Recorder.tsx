import { useRef, useState } from "react";
import { Button, Card, Collapse, Divider, Space, Typography } from "antd";

export function Recorder({ speech_id }: { speech_id: number }) {
  const [recording, setRecording] = useState<boolean>(false);
  const mediaRecorderRef = useRef<MediaRecorder>(undefined);
  const audioChunksRef = useRef<Blob[]>([]);
  const [convertedText, setConvertedText] = useState("");

  const sendRecording = async () => {};

  const startRecording = async () => {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    mediaRecorderRef.current = new MediaRecorder(stream);
    mediaRecorderRef.current.start(30_000);
    mediaRecorderRef.current.ondataavailable = (e) => {
      console.log(e.data);
      audioChunksRef.current.push(e.data);
    };
    mediaRecorderRef.current.onstop = async () => {
      const audioBlob = new Blob(audioChunksRef.current, { type: "audio/wav" });
      downloadWAV(audioBlob);
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
    // TODO lack of api
    setConvertedText(convertedText + "recorded text.");
  };

  return (
    <Card title={"实时语音转录功能"}>
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
      <Divider />
      <Collapse
        items={[
          {
            key: "1",
            label: "点击查看实时转录内容",
            children: (
              <div className={"max-h-96 overflow-auto"}>
                {convertedText === "" ? "暂无内容" : convertedText}
              </div>
            ),
          },
        ]}
      />
    </Card>
  );
}
