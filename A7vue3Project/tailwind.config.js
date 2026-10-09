/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#165DFF',
          light: '#3C7EFF',
          dark: '#0E42D6',
          bg: 'rgba(22, 93, 255, 0.08)',
          50: '#E8F0FF',
          100: '#C9DBFF',
          200: '#93B5FF',
          300: '#5D8EFF',
          400: '#3C7EFF',
          500: '#165DFF',
          600: '#0E42D6',
          700: '#0A33A8',
          800: '#07257A',
          900: '#04174D',
        },
        success: '#00B42A',
        warning: '#FF7D00',
        danger: '#F53F3F',
        purple: '#722ED1',
        pink: '#EB2F96',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
      },
      borderRadius: {
        'card': '16px',
        'btn': '8px',
      },
      boxShadow: {
        'card': '0 1px 2px rgba(0, 0, 0, 0.04)',
        'card-hover': '0 8px 24px rgba(0, 0, 0, 0.12)',
        'card-primary': '0 8px 30px rgba(22, 93, 255, 0.12)',
        'glow': '0 0 20px rgba(22, 93, 255, 0.15)',
      },
      animation: {
        'fade-in-up': 'fadeInUp 0.5s ease forwards',
        'fade-in': 'fadeIn 0.4s ease forwards',
        'scale-in': 'scaleIn 0.3s ease forwards',
        'float': 'float 4s ease-in-out infinite',
      },
    },
  },
  plugins: [],
}
