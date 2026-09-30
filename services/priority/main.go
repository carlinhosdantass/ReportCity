package main

import (
	"fmt"
	"log"
	"net/http"
)

func healthHandler(w http.ResponseWriter, _ *http.Request) {
	_, _ = fmt.Fprint(w, "OK")
}

func main() {
	mux := http.NewServeMux()
	mux.HandleFunc("GET /health", healthHandler)

	log.Fatal(http.ListenAndServe(":8081", mux))
}
