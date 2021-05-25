package main

import (
	"crypto/tls"
	"log"
	"net"
	"net/http"
	"time"

	"golang.org/x/net/http2"
)

const url = "http://localhost:7777/test"

func main() {
	{
		client := http.Client{
			Transport: &http2.Transport{
				AllowHTTP: true,
				DialTLS: func(network, addr string, cfg *tls.Config) (net.Conn, error) {
					return net.Dial(network, addr)
				},
			},
		}

		for i := 0; i < 10000; i++ {
			// GET
			{
				resp, err := client.Get(url)
				if err != nil {
					log.Fatalf("EXIT: error during get: %+v", err)
				}
				log.Printf("status=%d\n", resp.StatusCode)

				defer resp.Body.Close()
			}
			//time.Sleep(1 * time.Millisecond)
		}
	}
	// 	// GET
	// 	{
	// 		resp, err := client.Get(url)
	// 		if err != nil {
	// 			log.Fatalf("error get: %+v", err)
	// 		}
	// 		log.Printf("status=%d\n", resp.StatusCode)

	// 		defer resp.Body.Close()
	// 	}

	// 	time.Sleep(1 * time.Second)

	// 	//client.CloseIdleConnections()

	waitTime := 20 * time.Second
	log.Printf("Wait %s before exiting..\n", waitTime)
	time.Sleep(waitTime)
	log.Printf("Exiting..\n")
}
