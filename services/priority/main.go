package main

import (
	"log"
	"net/http"
)

func healthHandler(w http.ResponseWriter, _ *http.Request) {
	_, _ = w.Write([]byte("OK"))
}

func main() {
	http.HandleFunc("GET /health", healthHandler)
	log.Fatal(http.ListenAndServe(":8081", nil))
}
