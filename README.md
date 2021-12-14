# NLP+MAS(Multi Agent systems)Project_UniGeAssistant

## Introduction
UniGe Assistant is an android mobile app that students can use to chat with chatbot, ask about their classes and University events. The app has its own default categories (question, answer) and the user can teach it or ask the bot to connect to other bots to learn new categories. Also, the bot can send the question to the server app to answer it.

## App interface
The main interface contains the following: <br/><br/>
### Toolbar: 
it contains the app name, logout button and menu button that has one option to clear the chats.<br/>
###  Chat area: 
in this area the app will display the chat between the user and the bot.<br/>
###  Input area: 
this area contains the input text control that is used to type the chat text and an enter button to submit the text..<br/><br/>
<kbd>![alt text](https://github.com/ZiadJanpih/Natural-Language-Processing-And-Multi-Agent-systems-Project-_UniGeAssistant/blob/main/chat1.jpg)</kbd> 
<br/>
  
  ## UniGe Assistant processing mechanism
  After the user has typed the text in the text control and pressed enter. the app will perform the following steps to answer the user chat.<br/>
  
###	General Scenario
1-Save the user input into the app database.<br/>
2-Normalize the input using the normalization list that “Pandora chatbot” use.<br/>
3-Sentences breaking by (.,!,?).<br/>
4-find matched category for each of the sentences after “Sentences breaking”.<br/>
5-If the app finds a matched category it will save the result into database and show it to the user.<br/>
6-If the matched category has a special type (for example: “classes_today”) the app sends a request to the admin app asking about the requested data.<br/>

###	No matching Scenario
The app will show the user a dialog asking him to choose an option as showed in the following picture.<br/>
<kbd>![alt text](https://github.com/ZiadJanpih/Natural-Language-Processing-And-Multi-Agent-systems-Project-_UniGeAssistant/blob/main/dialog.jpg)</kbd> 
<br/>

###	You will teach me
The app will show the user a new interface containing the question asked by him and a text area to define the appropriate answer.<br/>
the user has also the ability to modify the question.<br/>
<kbd>![alt text](https://github.com/ZiadJanpih/Natural-Language-Processing-And-Multi-Agent-systems-Project-_UniGeAssistant/blob/main/answer%20question.jpg)</kbd> 
<br/>
