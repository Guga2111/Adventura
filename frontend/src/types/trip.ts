export interface Trip {
  id: string;
  name: string;
  coverImageUrl: string;
  startDate: string;
  endDate: string;
  placesCount: number;
  owner: {
    name: string;
    avatarUrl: string | null;
  };
}
