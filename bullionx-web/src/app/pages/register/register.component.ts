// path: bullionx-web/src/app/pages/register/register.component.ts
import { Component, OnInit, OnDestroy, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../core/auth/auth.service';
import { RegisterRequest } from '../../core/auth/auth.models';

interface Candle {
  x: number;
  open: number;
  high: number;
  low: number;
  close: number;
  timestamp: number;
  opacity: number;
  speed: number;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('candlestickCanvas', { static: false }) canvasRef!: ElementRef<HTMLCanvasElement>;

  form: FormGroup;
  isLoading = false;
  showPassword = false;
  isMobile = false;
  success = false;

  private ctx!: CanvasRenderingContext2D;
  private candles: Candle[] = [];
  private animationId?: number;
  private basePrice = 100;
  private time = 0;
  private mouseX = 0;
  private mouseY = 0;
  private targetMouseX = 0;
  private targetMouseY = 0;
  private scrollSpeed = 1.5;
  private animationStarted = false;

  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.form = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  ngOnInit(): void {
    this.checkIfMobile();
    window.addEventListener('resize', this.handleResize);
    if (!this.isMobile) this.initializeCandles();
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      if (!this.isMobile && this.canvasRef?.nativeElement) {
        this.setupCanvas();
        this.initializeCandles();
        this.addEventListeners();
        this.startAnimation();
      }
    }, 100);
  }

  ngOnDestroy(): void {
    if (this.animationId) cancelAnimationFrame(this.animationId);
    this.removeEventListeners();
    window.removeEventListener('resize', this.handleResize);
  }

  // UI
  togglePasswordVisibility(): void { this.showPassword = !this.showPassword; }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.isLoading = true;
    const req = this.form.value as RegisterRequest;
    this.authService.register(req).subscribe({
      next: () => { this.isLoading = false; this.success = true; },
      error: () => { this.isLoading = false; }
    });
  }

  // Responsiveness
  private checkIfMobile(): void { this.isMobile = window.innerWidth <= 768; }

  private handleResize = (): void => {
    const wasMobile = this.isMobile;
    this.checkIfMobile();

    if (wasMobile !== this.isMobile) {
      if (this.isMobile && this.animationId) {
        cancelAnimationFrame(this.animationId); this.animationId = undefined;
      } else if (!this.isMobile && !this.animationId && this.canvasRef) {
        this.setupCanvas();
        this.initializeCandles();
        this.startAnimation();
      }
    } else if (!this.isMobile && this.canvasRef?.nativeElement) {
      this.setupCanvas();
    }
  };

  // Canvas / animation
  private startAnimation(): void {
    if (this.animationId) cancelAnimationFrame(this.animationId);
    this.animationStarted = true;
    this.animate();
  }

  private initializeCandles(): void {
    const canvas = this.canvasRef?.nativeElement;
    if (!canvas) return;

    const width = parseInt(canvas.style.width || '800', 10);
    const candleSpacing = 20;
    const numCandles = Math.floor(width / candleSpacing) + 10;

    for (let i = 0; i < numCandles; i++) {
      const trend = Math.sin(i * 0.15) * 30;
      const volatility = Math.sin(i * 0.3) * 20 + Math.random() * 15;
      const open = this.basePrice + trend + (Math.random() - 0.5) * volatility;
      const close = open + (Math.random() - 0.5) * 10;
      const wickExtension = Math.random() * 20 + 15;
      const high = Math.max(open, close) + Math.random() * wickExtension;
      const low = Math.min(open, close) - Math.random() * wickExtension;

      this.candles.push({
        x: i * candleSpacing,
        open, high, low, close,
        timestamp: Date.now() - (numCandles - i) * 1000,
        opacity: 1,
        speed: this.scrollSpeed
      });

      this.basePrice = close;
    }
  }

  private setupCanvas(): void {
    const canvas = this.canvasRef.nativeElement;
    const parent = canvas.parentElement;
    if (!parent) return;

    const dpr = window.devicePixelRatio || 1;
    const rect = parent.getBoundingClientRect();

    canvas.width = rect.width * dpr;
    canvas.height = rect.height * dpr;
    canvas.style.width = `${rect.width}px`;
    canvas.style.height = `${rect.height}px`;

    this.ctx = canvas.getContext('2d')!;
    this.ctx.scale(dpr, dpr);

    this.mouseX = rect.width / 2;
    this.mouseY = rect.height / 2;
    this.targetMouseX = this.mouseX;
    this.targetMouseY = this.mouseY;
  }

  private addEventListeners(): void {
    const parent = this.canvasRef.nativeElement.parentElement;
    if (parent) {
      parent.addEventListener('mousemove', this.handleMouseMove);
      parent.addEventListener('touchmove', this.handleTouchMove);
    }
  }

  private removeEventListeners(): void {
    const parent = this.canvasRef?.nativeElement?.parentElement;
    if (parent) {
      parent.removeEventListener('mousemove', this.handleMouseMove);
      parent.removeEventListener('touchmove', this.handleTouchMove);
    }
  }

  private handleMouseMove = (e: MouseEvent): void => {
    const rect = (e.currentTarget as HTMLElement).getBoundingClientRect();
    this.targetMouseX = e.clientX - rect.left;
    this.targetMouseY = e.clientY - rect.top;
  };

  private handleTouchMove = (e: TouchEvent): void => {
    if (e.touches.length > 0) {
      const rect = (e.currentTarget as HTMLElement).getBoundingClientRect();
      this.targetMouseX = e.touches[0].clientX - rect.left;
      this.targetMouseY = e.touches[0].clientY - rect.top;
    }
  };

  private animate = (): void => {
    if (this.animationStarted && !this.isMobile) this.animationId = requestAnimationFrame(this.animate);
    if (!this.canvasRef?.nativeElement || !this.ctx || this.isMobile) return;

    this.time += 0.01;
    this.mouseX += (this.targetMouseX - this.mouseX) * 0.1;
    this.mouseY += (this.targetMouseY - this.mouseY) * 0.1;

    this.updateCandles();
    this.draw();
  };

  private updateCandles(): void {
    const canvas = this.canvasRef?.nativeElement;
    if (!canvas) return;

    const width = canvas.style.width ? parseInt(canvas.style.width, 10) : 800;
    const candleSpacing = 20;

    this.candles.forEach(c => { c.x -= this.scrollSpeed; });
    this.candles = this.candles.filter(c => c.x > -candleSpacing * 2);

    while (this.candles.length === 0 || this.candles[this.candles.length - 1].x < width + candleSpacing * 2) {
      const last = this.candles[this.candles.length - 1];
      const newX = last ? last.x + candleSpacing : width;

      const trendPhase = this.time * 2;
      const majorTrend = Math.sin(trendPhase) * 30;
      const minorTrend = Math.sin(trendPhase * 3) * 15;
      const microVolatility = (Math.random() - 0.5) * 20;

      const previousClose = last ? last.close : this.basePrice;
      const open = previousClose + (Math.random() - 0.5) * 5;
      const closeChange = majorTrend * 0.1 + minorTrend * 0.05 + microVolatility * 0.1;
      const close = open + closeChange;

      const volatility = 10 + Math.abs(Math.sin(this.time * 5)) * 20;
      const high = Math.max(open, close) + Math.random() * volatility + 5;
      const low = Math.min(open, close) - Math.random() * volatility - 5;

      this.candles.push({
        x: newX,
        open, high, low, close,
        timestamp: Date.now(),
        opacity: 0,
        speed: this.scrollSpeed
      });

      if (this.candles.length > 100) break;
    }

    this.candles.forEach(c => { if (c.opacity < 1) c.opacity = Math.min(1, c.opacity + 0.08); });
  }

  private draw(): void {
    const canvas = this.canvasRef?.nativeElement;
    if (!canvas || !this.ctx) return;

    const width = canvas.style.width ? parseInt(canvas.style.width, 10) : canvas.width;
    const height = canvas.style.height ? parseInt(canvas.style.height, 10) : canvas.height;

    this.ctx.clearRect(0, 0, width, height);

    if (this.candles.length < 2) { this.initializeCandles(); return; }

    const padding = 60;
    const chartHeight = height - padding * 2;
    const candleWidth = 8;

    const visible = this.candles.filter(c => c.x > -50 && c.x < width + 50);
    if (visible.length === 0) { this.initializeCandles(); return; }

    const prices = visible.flatMap(c => [c.high, c.low]);
    const minPrice = Math.min(...prices) - 10;
    const maxPrice = Math.max(...prices) + 10;
    const priceRange = maxPrice - minPrice;
    const priceScale = chartHeight / priceRange;

    this.drawGrid(width, height, padding);

    visible.forEach(c => {
      const x = c.x;
      const yOpen = padding + (maxPrice - c.open) * priceScale;
      const yClose = padding + (maxPrice - c.close) * priceScale;
      const yHigh = padding + (maxPrice - c.high) * priceScale;
      const yLow = padding + (maxPrice - c.low) * priceScale;

      if (x < -candleWidth || x > width + candleWidth) return;

      const center = (yOpen + yClose) / 2;
      const distance = Math.hypot(x - this.mouseX, center - this.mouseY);
      const influence = Math.max(0, 1 - distance / 120);

      const isGreen = c.close >= c.open;
      const baseColor = isGreen ? '#00D632' : '#FF4747';
      const glowColor = isGreen ? 'rgba(0, 214, 50, 0.4)' : 'rgba(255, 71, 71, 0.4)';
      const alpha = c.opacity * (0.7 + influence * 0.3);

      this.ctx.save();
      this.ctx.strokeStyle = baseColor;
      this.ctx.lineWidth = 1.5 + influence * 0.5;
      this.ctx.globalAlpha = alpha;
      if (influence > 0.1) { this.ctx.shadowBlur = 15 + influence * 25; this.ctx.shadowColor = glowColor; }

      this.ctx.beginPath();
      this.ctx.moveTo(x, yHigh);
      this.ctx.lineTo(x, yLow);
      this.ctx.stroke();

      const bodyH = Math.abs(yClose - yOpen) || 2;
      const bodyY = Math.min(yOpen, yClose);
      const w = candleWidth * (1 + influence * 0.4);

      const gradient = this.ctx.createLinearGradient(x - w / 2, bodyY, x + w / 2, bodyY + bodyH);
      gradient.addColorStop(0, baseColor);
      gradient.addColorStop(0.5, isGreen ? '#00FF3F' : '#FF6B6B');
      gradient.addColorStop(1, baseColor);

      this.ctx.fillStyle = gradient;
      this.ctx.fillRect(x - w / 2, bodyY, w, bodyH);
      this.ctx.restore();
    });

    const vignette = this.ctx.createRadialGradient(width / 2, height / 2, 0, width / 2, height / 2, Math.max(width, height) / 2);
    vignette.addColorStop(0, 'rgba(0, 0, 0, 0)');
    vignette.addColorStop(1, 'rgba(0, 0, 0, 0.3)');
    this.ctx.fillStyle = vignette;
    this.ctx.fillRect(0, 0, width, height);
  }

  private drawGrid(width: number, height: number, padding: number): void {
    this.ctx.strokeStyle = 'rgba(255, 255, 255, 0.02)';
    this.ctx.lineWidth = 1;

    const horizontalLines = 8;
    for (let i = 0; i <= horizontalLines; i++) {
      const y = padding + (height - padding * 2) / horizontalLines * i;
      this.ctx.beginPath();
      this.ctx.moveTo(0, y);
      this.ctx.lineTo(width, y);
      this.ctx.stroke();
    }

    const verticalSpacing = 50;
    const offset = (this.time * this.scrollSpeed * 10) % verticalSpacing;
    for (let i = -1; i < width / verticalSpacing + 2; i++) {
      const x = i * verticalSpacing - offset;
      this.ctx.beginPath();
      this.ctx.moveTo(x, 0);
      this.ctx.lineTo(x, height);
      this.ctx.stroke();
    }
  }
}
