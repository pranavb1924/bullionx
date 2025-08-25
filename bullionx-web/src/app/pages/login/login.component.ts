// path: bullionx-web/src/app/pages/login/login.component.ts
import { Component, OnInit, OnDestroy, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

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
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('candlestickCanvas', { static: false }) canvasRef!: ElementRef<HTMLCanvasElement>;

  form!: FormGroup;
  isLoading = false;
  showPassword = false;
  isMobile = false;

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

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(10)]],
    });
  }

  ngOnInit(): void {
    this.isMobile = window.innerWidth <= 768;
    window.addEventListener('resize', this.handleResize);
    if (!this.isMobile) this.initCandles();
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      if (!this.isMobile && this.canvasRef?.nativeElement) {
        this.setupCanvas();
        this.initCandles();
        this.addEvents();
        this.startAnim();
      }
    }, 100);
  }

  ngOnDestroy(): void {
    if (this.animationId) cancelAnimationFrame(this.animationId);
    this.removeEvents();
    window.removeEventListener('resize', this.handleResize);
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.isLoading = true;
    this.auth.login(this.form.value as any).subscribe({
      next: res => {
        this.auth.saveTokens(res);
        this.isLoading = false;
        this.router.navigateByUrl('/dashboard');
      },
      error: () => {
        this.isLoading = false;
        console.error('Invalid email / password');
      }
    });
  }

  private handleResize = (): void => {
    const wasMobile = this.isMobile;
    this.isMobile = window.innerWidth <= 768;

    if (wasMobile !== this.isMobile) {
      if (this.isMobile && this.animationId) {
        cancelAnimationFrame(this.animationId);
        this.animationId = undefined;
      } else if (!this.isMobile && !this.animationId && this.canvasRef) {
        this.setupCanvas();
        this.initCandles();
        this.startAnim();
      }
    } else if (!this.isMobile && this.canvasRef?.nativeElement) {
      this.setupCanvas();
    }
  };

  private addEvents(): void {
    const parent = this.canvasRef.nativeElement.parentElement;
    if (parent) {
      parent.addEventListener('mousemove', this.onMouseMove);
      parent.addEventListener('touchmove', this.onTouchMove);
    }
  }

  private removeEvents(): void {
    const parent = this.canvasRef?.nativeElement?.parentElement;
    if (parent) {
      parent.removeEventListener('mousemove', this.onMouseMove);
      parent.removeEventListener('touchmove', this.onTouchMove);
    }
  }

  private onMouseMove = (e: MouseEvent): void => {
    const r = (e.currentTarget as HTMLElement).getBoundingClientRect();
    this.targetMouseX = e.clientX - r.left;
    this.targetMouseY = e.clientY - r.top;
  };

  private onTouchMove = (e: TouchEvent): void => {
    if (e.touches.length > 0) {
      const r = (e.currentTarget as HTMLElement).getBoundingClientRect();
      this.targetMouseX = e.touches[0].clientX - r.left;
      this.targetMouseY = e.touches[0].clientY - r.top;
    }
  };

  private setupCanvas(): void {
    const canvas = this.canvasRef.nativeElement;
    const parent = canvas.parentElement!;
    const dpr = window.devicePixelRatio || 1;
    const rect = parent.getBoundingClientRect();

    canvas.width = rect.width * dpr;
    canvas.height = rect.height * dpr;
    canvas.style.width = `${rect.width}px`;
    canvas.style.height = `${rect.height}px`;

    this.ctx = canvas.getContext('2d')!;
    this.ctx.scale(dpr, dpr);

    this.mouseX = this.targetMouseX = rect.width / 2;
    this.mouseY = this.targetMouseY = rect.height / 2;
  }

  private initCandles(): void {
    const canvas = this.canvasRef?.nativeElement;
    if (!canvas) return;

    const width = parseInt(canvas.style.width || '800', 10);
    const spacing = 20;
    const n = Math.floor(width / spacing) + 10;

    for (let i = 0; i < n; i++) {
      const trend = Math.sin(i * 0.15) * 30;
      const vol = Math.sin(i * 0.3) * 20 + Math.random() * 15;
      const open = this.basePrice + trend + (Math.random() - 0.5) * vol;
      const close = open + (Math.random() - 0.5) * 10;

      const wick = Math.random() * 20 + 15;
      const high = Math.max(open, close) + Math.random() * wick;
      const low = Math.min(open, close) - Math.random() * wick;

      this.candles.push({
        x: i * spacing,
        open,
        high,
        low,
        close,
        timestamp: Date.now(),
        opacity: 1,
        speed: this.scrollSpeed
      });

      this.basePrice = close;
    }
  }

  private startAnim(): void {
    if (this.animationId) cancelAnimationFrame(this.animationId);
    this.animationStarted = true;
    this.animate();
  }

  private animate = (): void => {
    if (this.animationStarted && !this.isMobile) {
      this.animationId = requestAnimationFrame(this.animate);
    }
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
    const spacing = 20;

    this.candles.forEach(c => { c.x -= this.scrollSpeed; });
    this.candles = this.candles.filter(c => c.x > -spacing * 2);

    while (this.candles.length === 0 || this.candles[this.candles.length - 1].x < width + spacing * 2) {
      const last = this.candles[this.candles.length - 1];
      const newX = last ? last.x + spacing : width;

      const phase = this.time * 2;
      const maj = Math.sin(phase) * 30;
      const min = Math.sin(phase * 3) * 15;
      const micro = (Math.random() - 0.5) * 20;

      const prev = last ? last.close : this.basePrice;
      const open = prev + (Math.random() - 0.5) * 5;
      const close = open + (maj * 0.1 + min * 0.05 + micro * 0.1);

      const vol = 10 + Math.abs(Math.sin(this.time * 5)) * 20;
      const high = Math.max(open, close) + Math.random() * vol + 5;
      const low = Math.min(open, close) - Math.random() * vol - 5;

      this.candles.push({
        x: newX,
        open,
        high,
        low,
        close,
        timestamp: Date.now(),
        opacity: 0,
        speed: this.scrollSpeed
      });

      if (this.candles.length > 100) break;
    }

    this.candles.forEach(c => {
      if (c.opacity < 1) c.opacity = Math.min(1, c.opacity + 0.08);
    });
  }

  private draw(): void {
    const canvas = this.canvasRef!.nativeElement;
    const w = parseInt(canvas.style.width || `${canvas.width}`, 10);
    const h = parseInt(canvas.style.height || `${canvas.height}`, 10);

    this.ctx.clearRect(0, 0, w, h);

    if (this.candles.length < 2) {
      this.initCandles();
      return;
    }

    const pad = 60;
    const chartH = h - pad * 2;
    const candleW = 8;

    const visible = this.candles.filter(c => c.x > -50 && c.x < w + 50);
    if (visible.length === 0) {
      this.initCandles();
      return;
    }

    const prices = visible.flatMap(c => [c.high, c.low]);
    const min = Math.min(...prices) - 10;
    const max = Math.max(...prices) + 10;
    const scale = chartH / (max - min);

    this.drawGrid(w, h, pad);

    for (const c of visible) {
      const x = c.x;
      const yO = pad + (max - c.open) * scale;
      const yC = pad + (max - c.close) * scale;
      const yH = pad + (max - c.high) * scale;
      const yL = pad + (max - c.low) * scale;

      const cW = candleW;
      if (x < -cW || x > w + cW) continue;

      const center = (yO + yC) / 2;
      const dist = Math.hypot(x - this.mouseX, center - this.mouseY);
      const inf = Math.max(0, 1 - dist / 120);

      const green = c.close >= c.open;
      const base = green ? '#00D632' : '#FF4747';
      const glow = green ? 'rgba(0,214,50,0.4)' : 'rgba(255,71,71,0.4)';
      const alpha = c.opacity * (0.7 + inf * 0.3);

      this.ctx.save();
      this.ctx.strokeStyle = base;
      this.ctx.lineWidth = 1.5 + inf * 0.5;
      this.ctx.globalAlpha = alpha;
      if (inf > 0.1) { this.ctx.shadowBlur = 15 + inf * 25; this.ctx.shadowColor = glow; }

      this.ctx.beginPath();
      this.ctx.moveTo(x, yH);
      this.ctx.lineTo(x, yL);
      this.ctx.stroke();

      const bodyH = Math.abs(yC - yO) || 2;
      const bodyY = Math.min(yO, yC);
      const wC = cW * (1 + inf * 0.4);
      const grad = this.ctx.createLinearGradient(x - wC / 2, bodyY, x + wC / 2, bodyY + bodyH);
      grad.addColorStop(0, base);
      grad.addColorStop(0.5, green ? '#00FF3F' : '#FF6B6B');
      grad.addColorStop(1, base);
      this.ctx.fillStyle = grad;
      this.ctx.fillRect(x - wC / 2, bodyY, wC, bodyH);
      this.ctx.restore();
    }

    const vignette = this.ctx.createRadialGradient(w / 2, h / 2, 0, w / 2, h / 2, Math.max(w, h) / 2);
    vignette.addColorStop(0, 'rgba(0,0,0,0)');
    vignette.addColorStop(1, 'rgba(0,0,0,0.3)');
    this.ctx.fillStyle = vignette;
    this.ctx.fillRect(0, 0, w, h);
  }

  private drawGrid(w: number, h: number, pad: number): void {
    this.ctx.strokeStyle = 'rgba(255,255,255,0.02)';
    this.ctx.lineWidth = 1;

    for (let i = 0; i <= 8; i++) {
      const y = pad + (h - pad * 2) / 8 * i;
      this.ctx.beginPath();
      this.ctx.moveTo(0, y);
      this.ctx.lineTo(w, y);
      this.ctx.stroke();
    }

    const spacing = 50;
    const offset = (this.time * this.scrollSpeed * 10) % spacing;

    for (let i = -1; i < w / spacing + 2; i++) {
      const x = i * spacing - offset;
      this.ctx.beginPath();
      this.ctx.moveTo(x, 0);
      this.ctx.lineTo(x, h);
      this.ctx.stroke();
    }
  }
}
