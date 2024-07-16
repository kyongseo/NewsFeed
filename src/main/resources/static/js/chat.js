//const ws = new WebSocket("ws://localhost:80/ws"); // 처음 연결할 떄 ws 라고 지정했음
const ws = new WebSocket("ws://"+window.location.host+"/ws");
//const ws = new WebSocket("ws://localhost/ws"); // 처음 연결할 떄 ws 라고 지정했음

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