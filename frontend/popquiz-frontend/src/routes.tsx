import { Route, Routes } from "react-router";
import { user_id_atom } from "./states/user.ts";
import { useAtomValue } from "jotai";
import { LoginPage, SignupPage } from "./pages/Login.tsx";
import { HomePage } from "./pages/Dashboard.tsx";
import { CoursePage } from "./pages/CoursePage.tsx";
import { SpeechPage } from "./pages/SpeechPage.tsx";
import {
  StudentQuestionPage,
  TeacherQuestionPage,
} from "./pages/QuestionPage.tsx";

export function MainRouter() {
  const user_id = useAtomValue(user_id_atom);
  if (user_id === -1) {
    return (
      <Routes>
        <Route path={"/"} element={<LoginPage />} />
        <Route path={"/signup"} element={<SignupPage />} />
        <Route path={"*"} element={<LoginPage />} />
      </Routes>
    );
  }
  return (
    <Routes>
      <Route path={"/"} element={<HomePage />} />
      <Route path={"/course/:course_id"} element={<CoursePage />} />
      <Route path={"/speech/:speech_id"} element={<SpeechPage />} />
      <Route
        path={"/speech/student/:speech_id"}
        element={<StudentQuestionPage />}
      />
      <Route
        path={"/question/teacher/:question_id"}
        element={<TeacherQuestionPage />}
      />
      <Route
        path={"/question/student/:question_id"}
        element={<StudentQuestionPage />}
      />
    </Routes>
  );
}
