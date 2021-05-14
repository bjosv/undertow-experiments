-module(hello_handler).

-export([init/2]).

init(Req0, State) ->
    io:format(user, "Request received: pid=~w streamid=~w~n", [maps:get(pid, Req0), maps:get(streamid, Req0)]),
    Req = cowboy_req:reply(200,
                           #{<<"content-type">> => <<"text/plain">>},
                           <<"Hello Erlang!">>,
                           Req0),
    {ok, Req, State}.


%% #{bindings => #{},body_length => 0,cert => undefined,has_body => false,headers => #{<<97,99,99,101,112,116,45,101,110,99,111,100,105,110,103>> => <<103,122,105,112>>,<<117,115,101,114,45,97,103,101,110,116>> => <<71,111,45,104,116,116,112,45,99,108,105,101,110,116,47,50,46,48>>},host => <<108,111,99,97,108,104,111,115,116>>,host_info => undefined,method => <<71,69,84>>,path => <<47,116,101,115,116>>,path_info => undefined,peer => {{127,0,0,1},40348},pid => <0.202.0>,port => 7777,qs => <<>>,ref => http,scheme => <<104,116,116,112>>,sock => {{127,0,0,1},7777},streamid => 63,version => 'HTTP/2'}
