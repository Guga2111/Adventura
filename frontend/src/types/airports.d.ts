declare module "airports" {
  interface Airport {
    iata:      string;
    name:      string;
    lat?:      string;
    lon?:      string;
    iso?:      string;
    type?:     string;
    status?:   number;
    continent?: string;
    size?:     string | null;
  }
  const airports: Airport[];
  export default airports;
}
