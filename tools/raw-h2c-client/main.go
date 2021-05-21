package main

import (
	"log"
	"sync"
	"time"

	"github.com/summerwind/h2spec/config"
	"github.com/summerwind/h2spec/spec"
	"golang.org/x/net/http2"
)

func sender(id int, c *config.Config, wg *sync.WaitGroup) {
	defer wg.Done()

	conn, err := spec.Dial(c)
	if err != nil {
		log.Printf("[%d] Dial failed: %s\n", id, err)
		return
	}
	log.Printf("[%d] Dial ok\n", id)
	defer conn.Close()

	err = conn.Handshake()
	if err != nil {
		log.Printf("[%d] Handshake failed: %s\n", id, err)
		return
	}

	// Streams initiated by a client MUST use odd-numbered stream identifiers
	var streamID uint32 = 1

	for i := 1; i <= 100; i++ {
		headers := spec.CommonHeaders(c)
		hp := http2.HeadersFrameParam{
			StreamID:      streamID,
			EndStream:     true,
			EndHeaders:    true,
			BlockFragment: conn.EncodeHeaders(headers),
		}
		//log.Printf("[%d] <%d> Send GET with streamid=%d\n", id, i, streamID)
		conn.WriteHeaders(hp)

		loop := true
		for loop {
			ev := conn.WaitEvent()
			switch event := ev.(type) {
			case spec.HeadersFrameEvent:
				log.Printf("[%d] <%d> Received %+v\n", id, i, ev)
				loop = false
				if i == 10 {
					wg.Add(1)
					go sender(id+1, c, wg)
				}
				if i == 20 {
					log.Printf("[%d] >>>>>>>>>>>> Close\n", id)
					return
				}
			case spec.DataFrameEvent:
				//fmt.Printf("Received %+v\n", ev)
			case spec.GoAwayFrameEvent:
				log.Printf("[%d] <%d> Received %+v, LastStreamID=%d, ErrCode=%d ***************************\n", id, i, ev, event.LastStreamID, event.ErrCode)
			case spec.TimeoutEvent:
				//fmt.Printf("Received %+v\n", ev)
			case spec.ConnectionClosedEvent:
				log.Printf("[%d] <%d> Received %+v\n", id, i, ev)
				return
			default:
				log.Printf("[%d] <%d> Unhandled event received %+v\n", id, i, ev)
				return
			}
		}

		streamID = streamID + 2
		time.Sleep(100 * time.Millisecond)
	}
}

func main() {

	c := &config.Config{
		Host:         "127.0.0.1",
		Port:         7777,
		Path:         "/",
		Timeout:      time.Duration(2) * time.Second,
		MaxHeaderLen: 4000,
		// JUnitReport:  junitReport,
		// Strict:       strict,
		// DryRun:       dryRun,
		// TLS:          tls,
		// Ciphers:      ciphers,
		// Insecure:     insecure,
		// Verbose:      verbose,
		// Sections:     args,
	}

	var wg sync.WaitGroup

	wg.Add(1)
	go sender(1, c, &wg)

	wg.Wait()
}
