// 1. child-process모듈의 spawn 취득
/*
const spawn = require('child_process').spawn;
const result_02 = spawn('python', ['function_args.py', '카레유', '20']);

result_02.stdout.on('data', (result)=>{
    console.log(result.toString());
});
console.log(result_02)
// 출력 결과 => "카레유 : 20"
*/
const express = require('express');
const {spawn} = require('child_process');
const {exec} = require('child_process');
const app = express();
const port = process.env.PORT || 3000

var bodyParser = require('body-parser');
app.use(bodyParser.json());

app.get('/', (req, res) => {
    console.log("연결!!")
    let dataToSend;
    const python = spawn('python', ['test3.py', '126.8966655', '37.4830969', '출발지이름', '127.0276368', '37.4979502', '도착지이름']);
    python.stdout.on('data', (data, error) => {
        if (error) console.log(error);
        dataToSend = data.toString('utf8');
  })
  python.on('close', (code) => {
    console.log("xxxxx")
     res.send(dataToSend);
  })
});

app.post('/path_find', (req, res) => {
    console.log("길찾기 실행!!")
    var json_send = {
        jsonArray : ''
    };
    let dataToSend;
    const python = spawn('python', ['test3.py', req.body.latitude_from, req.body.longitude_from, req.body.start, req.body.latitude_to, req.body.longitude_to, req.body.end]);
    python.stdout.on('data', (data, error) => {
        if (error) console.log(error);
        dataToSend = data.toString('utf8');
  })
  python.on('close', (code) => {
    console.log("파이썬 스크립트 실행 종료")
    console.log(dataToSend);
    console.log("여기에요");
    json_send.jsonArray =dataToSend; 
    res.send(json_send);
  })
});

/*
app.get('/test', (req, res) => {
    console.log("연결!!")
    let dataToSend;
    const python = spawn('python', ['function_args.py', '126.8966655', '37.4830969']);
    python.stdout.on('data', (data, error) => {
        if (error) console.log(error);
        dataToSend = data.toString('utf8');
  })
  python.on('close', (code) => {
    console.log("xxxxx")
     res.send(dataToSend);
  })
});
*/
app.get('/test', (req, res) => {
    exec('python test.py', (err, stdout, stderr) => {
        if (err){
            console.log(err)
        }else{
            console.log(stdout)
        }
    })
})

app.listen(3000, () => console.log('Listening on 3000'));
