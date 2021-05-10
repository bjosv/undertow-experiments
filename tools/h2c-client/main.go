package main

import (
	"crypto/tls"
	"log"
	"net"
	"net/http"
	"time"

	"golang.org/x/net/http2"
)

const url = "http://localhost:7777"

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

		// GET
		{
			resp, err := client.Get(url)
			if err != nil {
				log.Fatalf("error get: %+v", err)
			}
			log.Printf("status=%d\n", resp.StatusCode)

			defer resp.Body.Close()
		}

		time.Sleep(1 * time.Second)

		// GET
		{
			resp, err := client.Get(url)
			if err != nil {
				log.Fatalf("error get: %+v", err)
			}
			log.Printf("status=%d\n", resp.StatusCode)

			defer resp.Body.Close()
		}

		time.Sleep(8 * time.Second)

		// GET
		{
			resp, err := client.Get(url)
			if err != nil {
				log.Fatalf("error get: %+v", err)
			}
			log.Printf("status=%d\n", resp.StatusCode)

			defer resp.Body.Close()
		}

		//client.CloseIdleConnections()
	}
	// time.Sleep(4 * time.Second)
	// {
	// 	client := http.Client{
	// 		Transport: &http2.Transport{
	// 			AllowHTTP: true,
	// 			DialTLS: func(network, addr string, cfg *tls.Config) (net.Conn, error) {
	// 				return net.Dial(network, addr)
	// 			},
	// 		},
	// 	}

	// 	// GET
	// 	{
	// 		resp, err := client.Get(url)
	// 		if err != nil {
	// 			log.Fatalf("error get: %+v", err)
	// 		}
	// 		log.Printf("status=%d\n", resp.StatusCode)

	// 		defer resp.Body.Close()
	// 	}
	// }
	log.Printf("Sleep..\n")
	time.Sleep(20 * time.Second)
	log.Printf("Sleep.. done\n")
}
