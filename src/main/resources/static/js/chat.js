//const ws = new WebSocket("ws://localhost:80/ws"); // 처음 연결할 떄 ws 라고 지정했음
//const ws = new WebSocket("ws://"+window.location.host+"/ws");
const ws = new WebSocket("ws://localhost:8081/ws"); // 처음 연결할 떄 ws 라고 지정했음

ws.onmessage = ( event) => {
    const chatMessage = document.getElementById("chatMessages");
    const chatElement = document.createElement("p");
    chatElement.innerText = event.data;
    chatMessage.appendChild(chatElement);
    chatMessage.scrollTop = chatMessage.scrollHeight;
}


const sendMessage = () => {
    const message = document.getElementById("message").value;
    if (message.trim() !== ""){
        ws.send(message);
        document.getElementById("message").value = '';
        document.getElementById("message").focus();
    }
};

// 엔터가 입력 되었을 때 똑같이 동작되도록..
document.getElementById("message").addEventListener("keypress",(event)  =>  {
    if (event.key === "Enter") {
        sendMessage();
        event.preventDefault();
    }
});

// window.location.host
//const ws = new WebSocket("ws://localhost:80/ws");

//ws.onopen = function(event) {
//    console.log("WebSocket 연결 성공");
//};

//ws.onmessage = function(event) {
////    const chatElement = document.createElement("p");
//    chatElement.innerText = event.data;
//    chatMessage.appendChild(chatElement);
//    chatMessage.scrollTop = chatMessage.scrollHeight;
//};

//ws.onerror = function(event) {
 //   console.error("WebSocket 오류 발생:", event);
//};

//ws.onclose = function(event) {
  // console.log("WebSocket 연결 종료:", event);
//};

//const sendMessage = () => {
    //const message = document.getElementById("message").value;
   // if (message.trim() !== ""){
    //    ws.send(message);
   //     document.getElementById("message").value = '';
   //     document.getElementById("message").focus();
   // }
//};

//document.getElementById("message").addEventListener("keypress", (event) => {
    //if (event.key === "Enter") {
      //  sendMessage();
       // event.preventDefault();
    //}
//});
