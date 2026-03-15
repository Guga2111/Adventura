import { useEffect, useRef, useCallback } from "react";
import type { RefObject } from "react";
import Map from "ol/Map";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import OSM from "ol/source/OSM";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import Feature from "ol/Feature";
import Point from "ol/geom/Point";
import LineString from "ol/geom/LineString";
import { fromLonLat } from "ol/proj";
import Style from "ol/style/Style";
import CircleStyle from "ol/style/Circle";
import Fill from "ol/style/Fill";
import Stroke from "ol/style/Stroke";

export interface MapPoint {
  lon:   number;
  lat:   number;
  label: string;
  kind:  "airport" | "excursion";
}

export interface MapPath {
  from: [number, number]; // [lon, lat]
  to:   [number, number];
}

export function useOLMap(containerRef: RefObject<HTMLDivElement | null>) {
  const mapRef    = useRef<Map | null>(null);
  const vectorRef = useRef<VectorSource | null>(null);

  useEffect(() => {
    if (!containerRef.current || mapRef.current) return;

    const vectorSource = new VectorSource();
    vectorRef.current  = vectorSource;

    mapRef.current = new Map({
      target: containerRef.current,
      layers: [
        new TileLayer({ source: new OSM() }),
        new VectorLayer({
          source: vectorSource,
          style: (feature) => {
            const kind = feature.get("kind") as "airport" | "excursion" | "path";
            if (kind === "path") {
              return new Style({
                stroke: new Stroke({ color: "#6366f1", width: 2, lineDash: [6, 4] }),
              });
            }
            const color = kind === "airport" ? "#f59e0b" : "#10b981";
            return new Style({
              image: new CircleStyle({
                radius: 7,
                fill:   new Fill({ color }),
                stroke: new Stroke({ color: "#fff", width: 2 }),
              }),
            });
          },
        }),
      ],
      view: new View({ center: fromLonLat([0, 20]), zoom: 2 }),
    });

    return () => {
      mapRef.current?.setTarget(undefined);
      mapRef.current = null;
    };
  }, []);

  const setMapData = useCallback((points: MapPoint[], paths: MapPath[]) => {
    const source = vectorRef.current;
    if (!source) return;

    source.clear();

    for (const p of points) {
      const f = new Feature({ geometry: new Point(fromLonLat([p.lon, p.lat])) });
      f.set("kind", p.kind);
      f.set("label", p.label);
      source.addFeature(f);
    }

    for (const path of paths) {
      const f = new Feature({
        geometry: new LineString([fromLonLat(path.from), fromLonLat(path.to)]),
      });
      f.set("kind", "path");
      source.addFeature(f);
    }

    const extent = source.getExtent();
    if (extent && extent[0] !== Infinity) {
      mapRef.current?.getView().fit(extent as [number, number, number, number], { padding: [60, 60, 60, 60], duration: 600 });
    }
  }, []);

  const flyTo = useCallback((lon: number, lat: number, zoom = 10) => {
    mapRef.current?.getView().animate({ center: fromLonLat([lon, lat]), zoom, duration: 800 });
  }, []);

  return { setMapData, flyTo };
}
