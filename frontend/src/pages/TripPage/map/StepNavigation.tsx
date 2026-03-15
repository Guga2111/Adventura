import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";

interface StepNavigationProps {
  current: number; // 1-based
  total:   number;
  onPrev:  () => void;
  onNext:  () => void;
}

export function StepNavigation({ current, total, onPrev, onNext }: StepNavigationProps) {
  if (total === 0) return null;

  return (
    <div className="flex items-center gap-2">
      <Button
        size="icon"
        variant="secondary"
        onClick={onPrev}
        disabled={current <= 1}
        aria-label="Previous step"
        className="h-8 w-8 rounded-full shadow"
      >
        <ChevronLeft className="h-4 w-4" />
      </Button>

      <span className="min-w-[3rem] text-center text-sm font-medium text-muted-foreground tabular-nums">
        {current} / {total}
      </span>

      <Button
        size="icon"
        variant="secondary"
        onClick={onNext}
        disabled={current >= total}
        aria-label="Next step"
        className="h-8 w-8 rounded-full shadow"
      >
        <ChevronRight className="h-4 w-4" />
      </Button>
    </div>
  );
}
