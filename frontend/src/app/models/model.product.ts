export interface Product {
  id?: number;
  name?: string;
  details?: string;
  price?: number;
  carbonImpact?: number;
  ecoCertified?: boolean;
  sellerId?: number;
  imageUrl?: string; // Image link from Cloudinary or backend
}
