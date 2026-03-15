import { useMemo, useEffect, useState } from "react";
import { OLMap }               from "./map/OLMap";
import { StepNavigation }      from "./map/StepNavigation";
import { StepLabel }           from "./map/StepLabel";
import { LocationInfoPanel }   from "./map/LocationInfoPanel";
import { buildFlatTimeline }   from "./tripUtils";
import type { Flight }         from "@/types/flight";
import type { Excursion }      from "@/types/trip";

interface TripPageMapProps {
  flights:      Flight[];
  excursions:   Excursion[];
  activeStep:   number;
  onStepChange: (step: number) => void;
}

export function TripPageMap({ flights, excursions, activeStep, onStepChange }: TripPageMapProps) {
  const events      = useMemo(() => buildFlatTimeline(flights, excursions), [flights, excursions]);
  const activeEvent = events[activeStep - 1];
  const [panelOpen, setPanelOpen] = useState(true);

  useEffect(() => {
    // Re-open panel whenever the active step changes
    setPanelOpen(true);
  }, [activeStep]);

  useEffect(() => {
    function onKey(e: KeyboardEvent) {
      if (e.key === "ArrowLeft")  onStepChange(Math.max(1, activeStep - 1));
      if (e.key === "ArrowRight") onStepChange(Math.min(events.length, activeStep + 1));
    }
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [activeStep, events.length, onStepChange]);

  return (
    <div className="relative w-full h-full">
      {/* Step controls overlay — centered at top */}
      <div className="absolute top-3 left-1/2 -translate-x-1/2 z-10 flex flex-col items-center gap-1
                      bg-background/80 backdrop-blur-sm rounded-xl px-4 py-2 shadow-md">
        <StepLabel event={activeEvent} />
        <StepNavigation
          current={activeStep}
          total={events.length}
          onPrev={() => onStepChange(Math.max(1, activeStep - 1))}
          onNext={() => onStepChange(Math.min(events.length, activeStep + 1))}
        />
      </div>

      <OLMap events={events} activeStep={activeStep} />

      {panelOpen && (
        <LocationInfoPanel
          event={activeEvent}
          onClose={() => setPanelOpen(false)}
        />
      )}
    </div>
  );
}
