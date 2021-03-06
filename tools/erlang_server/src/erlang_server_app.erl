%%%-------------------------------------------------------------------
%% @doc erlang_server public API
%% @end
%%%-------------------------------------------------------------------

-module(erlang_server_app).
-behaviour(application).

-export([start/2, stop/1]).

start(_StartType, _StartArgs) ->
    Dispatch = cowboy_router:compile([
                                      {'_', [{"/test", hello_handler, []}]}
                                     ]),
    {ok, _} = cowboy:start_clear(http,
                                 [{port, 7777}],
                                 #{env => #{dispatch => Dispatch}}
                                ),
    io:format(user, "Server started~n", []),
    erlang_server_sup:start_link().

stop(_State) ->
	ok = cowboy:stop_listener(http).
