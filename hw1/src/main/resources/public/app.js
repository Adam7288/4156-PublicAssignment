var gameStarted = !1,
    type = '',
    port = window['location']['port'],
    host = window['location']['hostname'];
const queryString = window['location']['search'],
    urlParams = new URLSearchParams(queryString);
var player = urlParams['get']('p'),
    turn = 1,
    tracker = document['getElementById']('test');

function playTurn(_0x8743xb) {

    if (gameStarted) {
        x = _0x8743xb['id']['charAt'](0), y = _0x8743xb['id']['charAt'](1);
        var _0x8743xc = {
            x: x,
            y: y
        };
        $['ajax']({
            method: 'POST',
            data: _0x8743xc,
            url: '/move/' + player + '/',
            success: function(_0x8743xc) {
                var _0x8743xd = JSON['parse'](_0x8743xc);
                0 == _0x8743xd['moveValidity'] ? $('#startButton')['html']('<h3><p style="color:red;">' + _0x8743xd['message'] + '</p></h3>') : _0x8743xb['innerText'] = type
            }
        })
    }
}

function checkVictory() {
    victory = 0, gameStarted = !1, alert('Game Over!')
}
tracker['style']['display'] = 'none', 2 == player ? ($('#startGame')['hide'](), $('#playerTypes')['hide']()) : (player = 1, window['history']['replaceState'](null, null, '?p=' + player)), $('#title')['html']($('#title')['html']() + ' (Player ' + player + ')'), $('.playerType')['on']('click', function() {
    $('.playerType')['removeClass']('active'), $(this)['addClass']('active'), type = $(this)['text']()
}), $('#startGame')['click'](function() {
    if (typeSelected = $('.active'), 0 != typeSelected['length']) {
        $(this)['hide']();
        var _0x8743xb = {
            type: type
        };
        $['ajax']({
            method: 'POST',
            data: _0x8743xb,
            url: '/startgame',
            success: function(_0x8743xb) {
                JSON['parse'](_0x8743xb);
                window['history']['replaceState'](null, null, '?p=' + player), $('#playerTypes')['html']('<a target="_blank" href="http://' + host + ':' + port + '/joingame/">Link for Player 2</a>'), $('#startButton')['html']('<h3>Waiting for player 2 to join!</h3>')
            }
        })
    } else {
        alert('Please select an option!')
    }
});
const socket = new WebSocket('ws://localhost:8080/gameboard');

function updateUI(_0x8743xb) {
    var _0x8743xc = JSON['parse'](_0x8743xb);
    return turn = _0x8743xc['turn'], type = 2 == player ? _0x8743xc['p2']['type'] : _0x8743xc['p1']['type'], updateBoard(_0x8743xc['boardState']), 0 != _0x8743xc['winner'] ? ($('#startButton')['html']('<h3>Game Over! Player ' + _0x8743xc['winner'] + ' has won the game!</h3>'), void((gameStarted = !1))) : 1 == _0x8743xc['isDraw'] ? ($('#startButton')['html']('<h3>This game is a draw!</h3>'), void((gameStarted = !1))) : ($('#startButton')['html']('<h3>Player ' + _0x8743xc['turn'] + '\'s Turn</h3>'), $('#playerTypes')['hide'](), void((gameStarted = _0x8743xc['gameStarted'])))
}

function updateBoard(_0x8743xb) {
    for (i = 0; i < 3; i++) {
        for (j = 0; j < 3; j++) {
            0 != _0x8743xb[i][j] && (elementId = i.toString() + j.toString(), $('#' + elementId)['text'](_0x8743xb[i][j]['replace'](/\0/g, '')))
        }
    }
}
socket['addEventListener']('open', function(_0x8743xb) {}), socket['addEventListener']('message', function(_0x8743xb) {
    updateUI(_0x8743xb['data'])
})