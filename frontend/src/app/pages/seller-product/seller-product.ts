import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CloudinaryService } from '../../services/cloudinary';
import { ProductService } from '../../services/product';

@Component({
  selector: 'app-seller-product',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './seller-product.html'
})
export class SellerProduct {
  private fb = inject(FormBuilder);
  private cloud = inject(CloudinaryService);
  private productSvc = inject(ProductService);
  public router = inject(Router);   // <-- made public

  form = this.fb.group({
    name: ['', Validators.required],
    details: [''],
    price: [0, [Validators.required, Validators.min(0)]],
    carbonImpact: [0, [Validators.min(0)]],
    ecoCertified: [false]
  });

  selectedFile?: File;
  previewUrl: string | null = null;
  uploading = false;
  error: string | null = null;

  onFileChange(ev: Event) {
    const input = ev.target as HTMLInputElement;
    if (input.files && input.files.length) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = () => (this.previewUrl = reader.result as string);
      reader.readAsDataURL(this.selectedFile);
    }
  }

  submit() {
    if (this.form.invalid) return this.form.markAllAsTouched();
    this.uploading = true;
    this.error = null;

    const createPayload = (imageUrl?: string | null) => {
      const payload: any = {
        name: this.form.value.name,
        details: this.form.value.details,
        price: Number(this.form.value.price),
        carbonImpact: Number(this.form.value.carbonImpact),
        ecoRequested: Boolean(this.form.value.ecoCertified),
        imageUrl: imageUrl || null
      };
      this.productSvc.create(payload).subscribe({
        next: () => {
          this.uploading = false;
          alert('✅ Product submitted');
          this.router.navigate(['/seller/dashboard']);
        },
        error: err => {
          console.error(err);
          this.uploading = false;
          this.error = 'Failed to save product';
        }
      });
    };

    if (this.selectedFile) {
      this.cloud.uploadFile(this.selectedFile).subscribe({
        next: (res:any) => createPayload(res?.secure_url || res?.url || null),
        error: err => { console.error(err); this.uploading = false; this.error = 'Image upload failed'; }
      });
    } else {
      createPayload(null);
    }
  }
}
