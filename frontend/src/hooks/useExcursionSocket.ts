import { useEffect, useRef, useCallback } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

type MessageHandler = (data: unknown) => void;

const BASE_DELAY = 1_000;  // 1s
const MAX_DELAY  = 30_000; // 30s cap

export function useExcursionSocket(tripId: number, onMessage: MessageHandler) {
  const clientRef      = useRef<Client | null>(null);
  const onMessageRef   = useRef(onMessage);
  const attemptRef     = useRef(0);
  const reconnectTimer = useRef<ReturnType<typeof setTimeout> | null>(null);
  const activeRef      = useRef(false);

  // Keep the callback ref up-to-date without triggering reconnects
  useEffect(() => {
    onMessageRef.current = onMessage;
  }, [onMessage]);

  useEffect(() => {
    activeRef.current  = true;
    attemptRef.current = 0;

    // Exponential backoff with ±20% jitter to spread simultaneous reconnects
    function nextDelay() {
      const exp    = Math.min(BASE_DELAY * 2 ** attemptRef.current, MAX_DELAY);
      const jitter = exp * 0.2 * (Math.random() * 2 - 1);
      return Math.round(exp + jitter);
    }

    function connect() {
      if (!activeRef.current) return;

      const token  = localStorage.getItem("jwt");
      const wsBase = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

      const client = new Client({
        webSocketFactory: () => new SockJS(`${wsBase}/ws`),
        connectHeaders:   token ? { Authorization: `Bearer ${token}` } : {},
        reconnectDelay:   0, // disabled — we handle reconnection manually below

        onConnect: () => {
          attemptRef.current = 0; // reset backoff on successful connect
          client.subscribe(`/topic/trip/${tripId}/excursions`, (msg) => {
            try { onMessageRef.current(JSON.parse(msg.body)); } catch { /* ignore */ }
          });
        },

        onDisconnect: () => {
          if (!activeRef.current) return;
          const delay = nextDelay();
          attemptRef.current += 1;
          reconnectTimer.current = setTimeout(connect, delay);
        },

        onStompError: () => {
          if (!activeRef.current) return;
          const delay = nextDelay();
          attemptRef.current += 1;
          reconnectTimer.current = setTimeout(connect, delay);
        },
      });

      client.activate();
      clientRef.current = client;
    }

    connect();

    return () => {
      activeRef.current = false;
      if (reconnectTimer.current) clearTimeout(reconnectTimer.current);
      clientRef.current?.deactivate();
    };
  }, [tripId]);

  const sendUpdate = useCallback(
    (message: object) => {
      clientRef.current?.publish({
        destination: `/app/trip/${tripId}/excursions`,
        body: JSON.stringify(message),
      });
    },
    [tripId],
  );

  return { sendUpdate };
}
