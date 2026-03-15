import { useRef, useEffect } from "react";
import Map from "ol/Map";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import OSM from "ol/source/OSM";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import Feature from "ol/Feature";
import Point from "ol/geom/Point";
import { fromLonLat } from "ol/proj";
import Style from "ol/style/Style";
import CircleStyle from "ol/style/Circle";
import Fill from "ol/style/Fill";
import Stroke from "ol/style/Stroke";

interface MiniMapPreviewProps {
  lat: number;
  lon: number;
}

export function MiniMapPreview({ lat, lon }: MiniMapPreviewProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const mapRef = useRef<Map | null>(null);
  const sourceRef = useRef<VectorSource | null>(null);

  useEffect(() => {
    if (!containerRef.current || mapRef.current) return;

    const source = new VectorSource();
    sourceRef.current = source;

    mapRef.current = new Map({
      target: containerRef.current,
      layers: [
        new TileLayer({ source: new OSM() }),
        new VectorLayer({
          source,
          style: new Style({
            image: new CircleStyle({
              radius: 8,
              fill:   new Fill({ color: "#10b981" }),
              stroke: new Stroke({ color: "#fff", width: 2 }),
            }),
          }),
        }),
      ],
      view: new View({ center: fromLonLat([lon, lat]), zoom: 13 }),
      controls: [],
      interactions: [],
    });

    return () => {
      mapRef.current?.setTarget(undefined);
      mapRef.current = null;
    };
  }, []);

  useEffect(() => {
    if (!mapRef.current || !sourceRef.current) return;

    const center = fromLonLat([lon, lat]);
    mapRef.current.getView().animate({ center, zoom: 13, duration: 500 });

    sourceRef.current.clear();
    sourceRef.current.addFeature(
      new Feature({ geometry: new Point(center) })
    );
  }, [lat, lon]);

  return (
    <div
      ref={containerRef}
      className="w-full h-36 rounded-lg overflow-hidden border"
      aria-label="Location preview map"
    />
  );
}
