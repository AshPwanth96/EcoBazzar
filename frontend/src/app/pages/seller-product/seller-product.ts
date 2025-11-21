import { Component, inject, OnInit } from '@angular/core';
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
export class SellerProduct implements OnInit {
  private fb = inject(FormBuilder);
  private cloud = inject(CloudinaryService);
  private productSvc = inject(ProductService);
  public router = inject(Router);

  editing = false;
  editingId?: number;

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

  ngOnInit() {
    // read product from history.state (set by SellerDashboard.edit)
    const state: any = history.state;
    if (state && state.product) {
      const p = state.product;
      this.editing = true;
      this.editingId = p.id;
      this.form.patchValue({
        name: p.name || '',
        details: p.details || '',
        price: p.price ?? 0,
        carbonImpact: p.carbonImpact ?? 0,
        ecoCertified: !!p.ecoRequested
      });
      this.previewUrl = p.imageUrl || null;
    }
  }

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

    const sendPayload = (imageUrl?: string | null) => {
      const payload: any = {
        name: this.form.value.name,
        details: this.form.value.details,
        price: Number(this.form.value.price),
        carbonImpact: Number(this.form.value.carbonImpact),
        ecoRequested: Boolean(this.form.value.ecoCertified),
        imageUrl: imageUrl || null
      };

      const obs = this.editing && this.editingId
        ? this.productSvc.update(this.editingId, payload)
        : this.productSvc.create(payload);

      obs.subscribe({
        next: () => {
          this.uploading = false;
          alert(this.editing ? '✅ Product updated' : '✅ Product submitted');
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
        next: (res:any) => sendPayload(res?.secure_url || res?.url || null),
        error: err => { console.error(err); this.uploading = false; this.error = 'Image upload failed'; }
      });
    } else {
      sendPayload(this.previewUrl || null);
    }
  }
}
