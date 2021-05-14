# erlang_server

## Build

rebar3 compile

## Run

rebar3 shell

application:ensure_all_started(cowboy).
application:ensure_all_started(erlang_server).

## Trigger a graceful shutdown

sys:terminate(lists:nth(1,ranch:procs(http, connections)), goaway).
