
const express = require('express');
const {spawn} = require('child_process');
const {exec} = require('child_process');
const app = express();
const port = process.env.PORT || 3000

var bodyParser = require('body-parser');
app.use(bodyParser.json());

app.get('/', (req, res) => {
    let dataToSend;
    const python = spawn('python', ['test3.py', '126.8966655', '37.4830969', '출발지이름', '127.0276368', '37.4979502', '도착지이름']);
    python.stdout.on('data', (data, error) => {
        if (error) console.log(error);
        dataToSend = data.toString('utf8');
  })
  python.on('close', (code) => {
     res.send(dataToSend);
  })
});

app.post('/path_find', (req, res) => {
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
    json_send.jsonArray =dataToSend; 
    res.send(json_send);
  })
});

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
